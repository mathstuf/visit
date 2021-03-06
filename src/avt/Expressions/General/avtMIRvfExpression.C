/*****************************************************************************
*
* Copyright (c) 2000 - 2019, Lawrence Livermore National Security, LLC
* Produced at the Lawrence Livermore National Laboratory
* LLNL-CODE-442911
* All rights reserved.
*
* This file is  part of VisIt. For  details, see https://visit.llnl.gov/.  The
* full copyright notice is contained in the file COPYRIGHT located at the root
* of the VisIt distribution or at http://www.llnl.gov/visit/copyright.html.
*
* Redistribution  and  use  in  source  and  binary  forms,  with  or  without
* modification, are permitted provided that the following conditions are met:
*
*  - Redistributions of  source code must  retain the above  copyright notice,
*    this list of conditions and the disclaimer below.
*  - Redistributions in binary form must reproduce the above copyright notice,
*    this  list of  conditions  and  the  disclaimer (as noted below)  in  the
*    documentation and/or other materials provided with the distribution.
*  - Neither the name of  the LLNS/LLNL nor the names of  its contributors may
*    be used to endorse or promote products derived from this software without
*    specific prior written permission.
*
* THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT  HOLDERS AND CONTRIBUTORS "AS IS"
* AND ANY EXPRESS OR  IMPLIED WARRANTIES, INCLUDING,  BUT NOT  LIMITED TO, THE
* IMPLIED WARRANTIES OF MERCHANTABILITY AND  FITNESS FOR A PARTICULAR  PURPOSE
* ARE  DISCLAIMED. IN  NO EVENT  SHALL LAWRENCE  LIVERMORE NATIONAL  SECURITY,
* LLC, THE  U.S.  DEPARTMENT OF  ENERGY  OR  CONTRIBUTORS BE  LIABLE  FOR  ANY
* DIRECT,  INDIRECT,   INCIDENTAL,   SPECIAL,   EXEMPLARY,  OR   CONSEQUENTIAL
* DAMAGES (INCLUDING, BUT NOT  LIMITED TO, PROCUREMENT OF  SUBSTITUTE GOODS OR
* SERVICES; LOSS OF  USE, DATA, OR PROFITS; OR  BUSINESS INTERRUPTION) HOWEVER
* CAUSED  AND  ON  ANY  THEORY  OF  LIABILITY,  WHETHER  IN  CONTRACT,  STRICT
* LIABILITY, OR TORT  (INCLUDING NEGLIGENCE OR OTHERWISE)  ARISING IN ANY  WAY
* OUT OF THE  USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
* DAMAGE.
*
*****************************************************************************/

// ************************************************************************* //
//                           avtMIRvfExpression.C                            //
// ************************************************************************* //

#include <avtMIRvfExpression.h>

#include <math.h>

#include <vtkCellData.h>
#include <vtkDataArray.h>
#include <vtkDataSet.h>
#include <vtkIntArray.h>

#include <avtExprNode.h>
#include <ExprToken.h>

#include <avtCallback.h>
#include <avtMaterial.h>
#include <avtMetaData.h>
#include <avtSILRestrictionTraverser.h>

#include <DebugStream.h>
#include <ExpressionException.h>

#include <string>
#include <vector>

// ****************************************************************************
//  Method: avtMIRvfExpression constructor
//
//  Purpose:
//      Defines the constructor.  Note: this should not be inlined in the
//      header because it causes problems for certain compilers.
//
//  Programmer: Hank Childs
//  Creation:   December 31, 2004
//
// ****************************************************************************

avtMIRvfExpression::avtMIRvfExpression()
{
    issuedWarning = false;
}


// ****************************************************************************
//  Method: avtMIRvfExpression destructor
//
//  Purpose:
//      Defines the destructor.  Note: this should not be inlined in the header
//      because it causes problems for certain compilers.
//
//  Programmer: Hank Childs
//  Creation:   December 31, 2004
//
// ****************************************************************************

avtMIRvfExpression::~avtMIRvfExpression()
{
    ;
}


// ****************************************************************************
//  Method: avtMIRvfExpression::PreExecute
//
//  Purpose:
//      Reset the "issuedWarning" flag.
//
//  Programmer: Hank Childs
//  Creation:   December 31, 2004
//
// ****************************************************************************

void
avtMIRvfExpression::PreExecute(void)
{
    issuedWarning = false;
    avtSingleInputExpressionFilter::PreExecute();
}


// ****************************************************************************
//  Method: avtMIRvfExpression::DeriveVariable
//
//  Purpose:
//      Assigns the volume fraction to each point.
//
//  Arguments:
//      inDS      The input dataset.
//
//  Returns:      The derived variable.  The calling class must free this
//                memory.
//
//  Programmer:   Sean Ahern
//  Creation:     December 31, 2004
//
// ****************************************************************************

vtkDataArray *
avtMIRvfExpression::DeriveVariable(vtkDataSet *in_ds, int currentDomainsIndex)
{
    vtkDataArray *volume = in_ds->GetCellData()->GetArray(volume_name.c_str());
    vtkDataArray *zoneid = in_ds->GetCellData()->GetArray(zoneid_name.c_str());
    if (volume == NULL || zoneid == NULL)
    {
        EXCEPTION2(ExpressionException, outputVariableName, 
                    "The arguments to MIR VF were not created properly.");
    }
    vtkIntArray *matnum = (vtkIntArray *)
                           in_ds->GetCellData()->GetArray("avtSubsets");
    if (matnum == NULL)
    {
        EXCEPTION2(ExpressionException, outputVariableName, 
                   "MIR VF not able to locate materials");
    }

    //
    // Determine the biggest zone id.
    //
    vtkIdType ncells = in_ds->GetNumberOfCells();
    int biggest_zone = -1;
    for (vtkIdType i = 0 ; i < ncells ; i++)
    {
        int z = (int) zoneid->GetTuple1(i);
        biggest_zone = (z > biggest_zone ? z : biggest_zone);
    }
    double *total_vol = new double[biggest_zone+1];
    double *mat_vol   = new double[biggest_zone+1];
    for (int i = 0 ; i < biggest_zone+1 ; i++)
    {
        total_vol[i] = 0.;
        mat_vol[i]   = 0.;
    }

    //
    // Whole zones were broken into subzones by the MIR.  We want our
    // values to correspond to the whole zones.  So iterate over the subzones
    // and try to determine what the volumes are for the whole zones.  We want
    // to know the total volume for each whole zone and the volume devoted to
    // the materials specified.
    // 
    std::vector<bool> useMat;
    GetMaterialList(useMat, currentDomainsIndex);
    for (vtkIdType i = 0 ; i < ncells ; i++)
    {
        int z = (int) zoneid->GetTuple1(i);
        double vol = volume->GetTuple1(i);
        int mat = matnum->GetValue(i);
        total_vol[z] += vol;
        if (useMat[mat])
            mat_vol[z] += vol;
    }

    //
    // Now that we know the total volume and the volume for each material
    // for each whole zone, we can calculate the volume fraction and then
    // assign that volume fraction to each subzone.
    //
    vtkDataArray *rv = volume->NewInstance();
    rv->SetNumberOfTuples(ncells);
    for (vtkIdType i = 0 ; i < ncells ; i++)
    {
        int z = (int) zoneid->GetTuple1(i);
        if (total_vol[z] != 0.)  // should always be non-zero, but who knows?
            rv->SetTuple1(i, mat_vol[z] / total_vol[z]);
        else
            rv->SetTuple1(i, 0.);
    }

    delete [] mat_vol;
    delete [] total_vol;

    return rv;
}


// ****************************************************************************
//  Method: avtMIRvfExpression::GetMaterialList
//
//  Purpose:
//      Determines the materials we should use when calculating the MIR VF.
//
//  Notes:      This routine was taken wholly from avtMatvfExpression.
//
//  Programmer: Hank Childs
//  Creation:   January 1, 2005
//
// ****************************************************************************

void
avtMIRvfExpression::GetMaterialList(std::vector<bool> &useMat, int currentDomainsIndex)
{

    //
    // The 'currentDomainsIndex' is a data member of the base class that is
    // set to be the id of the current domain right before DeriveVariable is
    // called.  We need that index to make sure we are getting the right mat.
    //
    // The 'currentTimeState' is a data member of the base class that is
    // set to be the current timestep during ExamineContract. 
    // We need that timestep to make sure we are getting the right mat.
    //
    avtMaterial *mat = GetMetaData()->GetMaterial(currentDomainsIndex,
                                                  currentTimeState);
    if (mat == NULL)
    {
        EXCEPTION2(ExpressionException, outputVariableName, 
                   "Unable to match up material names.");
    }

    //
    // Try to match up the materials in the avtMaterial object with the
    // materials requested by the users.
    //
    size_t nmats = mat->GetNMaterials();
    useMat.resize(nmats, false);
    std::vector<bool>  matchedMatName(matNames.size(), false);
    std::vector<bool>  matchedMatIndex(matIndices.size(), false);
    for (size_t i = 0 ; i < nmats ; i++)
    {
        std::string currentMat = mat->GetMaterials()[i];
        for (size_t j = 0 ; j < matNames.size() ; j++)
        {
            if (currentMat == matNames[j])
            {
                useMat[i] = true;
                matchedMatName[j] = true;
            }
        }
        for (size_t j = 0 ; j < matIndices.size() ; j++)
        {
            char tmp[256];
            sprintf(tmp, "%d", matIndices[j]);

            std::string matname(tmp);
            if (currentMat == matname ||
                (currentMat.length() > matname.length() &&
                 currentMat.substr(0,matname.length() + 1) == (matname + " ")))
            {
                useMat[i] = true;
                matchedMatIndex[j] = true;
            }
        }
    }

    //
    // Make sure that we found every material requested.  If not, issue
    // a warning.
    //
    for (size_t i = 0 ; i < matNames.size() ; i++)
    {
        if (!matchedMatName[i])
        {
            const std::vector<std::string> &all_mats = 
                                                mat->GetCompleteMaterialList();
            bool matched = false;
            for (size_t j = 0 ; j < all_mats.size() ; j++)
            {
                if (matNames[i] == all_mats[j])
                {
                    matched = true;
                    break;
                }
            }
            if (!matched)
            {
                if (!issuedWarning)
                {
                    char warningString[100000];
                    sprintf(warningString, "Could not match up \"%s\" with "
                              "any materials when doing the matvf expression."
                              "\nList of valid materials is: ", 
                              matNames[i].c_str());
                    char *tmp = warningString + strlen(warningString);
                    for (size_t j = 0 ; j < all_mats.size() ; j++)
                    {
                        if (j < (all_mats.size()-1))
                            sprintf(tmp, "\"%s\", ", all_mats[j].c_str());
                        else
                            sprintf(tmp, "\"%s\".", all_mats[j].c_str());
                        tmp += strlen(tmp);
                    }
                    avtCallback::IssueWarning(warningString);
                    issuedWarning = true;
                }
            }
        }
    }
    for (size_t i = 0 ; i < matIndices.size() ; i++)
    {
        char tmp[256];
        sprintf(tmp, "%d", matIndices[i]);

        std::string matname(tmp);
        if (!matchedMatIndex[i])
        {
            const std::vector<std::string> &all_mats = 
                                                mat->GetCompleteMaterialList();
            bool matched = false;
            for (size_t j = 0 ; j < all_mats.size() ; j++)
            {
                if (matname == all_mats[j])
                {
                    matched = true;
                    break;
                }
            }
            if (!matched)
            {
                if (!issuedWarning)
                {
                    char warningString[100000];
                    sprintf(warningString, "Could not match up \"%s\" with "
                              "any materials when doing the matvf expression."
                              "\nList of valid materials is: ", 
                              matname.c_str());
                    char *tmp = warningString + strlen(warningString);
                    for (size_t j = 0 ; j < all_mats.size() ; j++)
                    {
                        if (j < (all_mats.size()-1))
                            sprintf(tmp, "\"%s\", ", all_mats[j].c_str());
                        else
                            sprintf(tmp, "\"%s\".", all_mats[j].c_str());
                        tmp += strlen(tmp);
                    }
                    avtCallback::IssueWarning(warningString);
                    issuedWarning = true;
                }
            }
        }
    }
}


// ****************************************************************************
//  Method: avtMIRvfExpression::ProcessArguments
//
//  Purpose:
//      Tells the first argument to go generate itself.  Parses the fourth
//      argument into a list of material names.
//
//  Notes:        This routine was directly taken from avtMatvfExpression.
//
//  Programmer:   Hank Childs
//  Creation:     December 31, 2004
//
//  Modifications:
//    Jeremy Meredith, Mon Jun 13 11:42:38 PDT 2005
//    Changed the way constant expressions work.
//
// ****************************************************************************
void
avtMIRvfExpression::ProcessArguments(ArgsExpr *args, ExprPipelineState *state)
{
    // Check the number of arguments
    std::vector<ArgExpr*> *arguments = args->GetArgs();
    size_t nargs = arguments->size();
    if (nargs != 4)
    {
        EXCEPTION2(ExpressionException, outputVariableName, 
             "avtMIRvfExpression: Incorrect # of "
             "arguments.  (mat-name, zone-id, volume, material list)."
             "To specify more than one material, use a list (e.g. [1,4,5:9].");
    }
    // Tell the first argument (material name) to create its filters.
    ArgExpr *firstarg = (*arguments)[0];
    avtExprNode *firstTree = dynamic_cast<avtExprNode*>(firstarg->GetExpr());
    firstTree->CreateFilters(state);

    // Tell the second argument (zone ids) to create its filters.
    ArgExpr *secondarg = (*arguments)[1];
    avtExprNode *secondTree = dynamic_cast<avtExprNode*>(secondarg->GetExpr());
    secondTree->CreateFilters(state);
    zoneid_name = secondarg->GetText();

    // Tell the third argument (volume) to create its filters.
    ArgExpr *thirdarg = (*arguments)[2];
    avtExprNode *thirdTree = dynamic_cast<avtExprNode*>(thirdarg->GetExpr());
    thirdTree->CreateFilters(state);
    volume_name = thirdarg->GetText();

    // Pull off the fourth argument and see if it's a string or a list.
    ArgExpr *fourtharg = (*arguments)[3];
    ExprParseTreeNode *fourthTree = fourtharg->GetExpr();
    std::string type = fourthTree->GetTypeName();
    if ((type != "IntegerConst") && (type != "StringConst") && (type != "List"))
    {
        debug5 << "avtMIRvfExpression: Second argument is not a constant or a "
               << "list: " << type.c_str() << endl;
        EXCEPTION2(ExpressionException, outputVariableName, 
                   "avtMIRvfExpression: Second argument is not a constant or a"
                   " list.");
    }

    if (type == "IntegerConst" || type == "StringConst")
    {
        // It's a single constant.
        AddMaterial(dynamic_cast<ConstExpr*>(fourthTree));
    }
    else
    {
        // It's a list.  Process all of them.
        ListExpr *list = dynamic_cast<ListExpr*>(fourthTree);
        std::vector<ListElemExpr*> *elems = list->GetElems();
        for(size_t i=0;i<elems->size();i++)
        {
            if ((*elems)[i]->GetEnd())
            {
                // it's a range
                ExprNode *begExpr  = (*elems)[i]->GetBeg();
                ExprNode *endExpr  = (*elems)[i]->GetEnd();
                ExprNode *skipExpr = (*elems)[i]->GetSkip();
                
                if (begExpr->GetTypeName() != "IntegerConst" ||
                    endExpr->GetTypeName() != "IntegerConst" ||
                    (skipExpr && skipExpr->GetTypeName() != "IntegerConst"))
                {
                    EXCEPTION2(ExpressionException, outputVariableName, 
                               "avtMIRvfExpression: "
                               "Range must contain integers.");
                }

                int beg  = dynamic_cast<IntegerConstExpr*>(begExpr)->GetValue();
                int end  = dynamic_cast<IntegerConstExpr*>(endExpr)->GetValue();
                int skip = !skipExpr ? 1 : 
                          dynamic_cast<IntegerConstExpr*>(skipExpr)->GetValue();

                if (skip <= 0 || beg > end)
                {
                    EXCEPTION2(ExpressionException, outputVariableName, 
                               "avtMIRvfExpression: "
                               "Range must be of the form beg:end[:skip].");
                }

                for (int m = beg; m <= end ; m += skip)
                    matIndices.push_back(m);
            }
            else
            {
                ExprNode *item = (*elems)[i]->GetItem();
                std::string type = item->GetTypeName();
                if (type != "IntegerConst" && type != "StringConst")
                {
                    debug5 << "avtMIRvfExpression: List element is not an "
                              "integer constant, a string constant, "
                              "or a list: " << type.c_str() << endl;
                    EXCEPTION2(ExpressionException, outputVariableName, 
                               "avtMIRvfExpression: "
                               "List element is not a int/string constant "
                               "or a list.");
                }

                AddMaterial(dynamic_cast<ConstExpr*>(item));
            }
        }
    }
}

// ****************************************************************************
//  Method:  avtMIRvfExpression::AddMaterial
//
//  Purpose:
//    Add a material by name or index to the list
//
//  Arguments:
//    c          The expression to turn into a material
//
//  Notes:        This routine was directly taken from avtMatvfExpression.
//
//  Programmer:  Hank Childs
//  Creation:    December 31, 2004
//
//  Modifications:
//    Jeremy Meredith, Mon Jun 13 11:42:38 PDT 2005
//    Changed the way constant expressions work.
//
// ****************************************************************************

void
avtMIRvfExpression::AddMaterial(ConstExpr *c)
{
    if (c->GetConstantType() == ConstExpr::String)
    {
        std::string matname = dynamic_cast<StringConstExpr*>(c)->GetValue();
        matNames.push_back(matname);
    }
    else // c->GetConstantType() == ConstExpr::Integer
    {
        int matindex = dynamic_cast<IntegerConstExpr*>(c)->GetValue();
        matIndices.push_back(matindex);
    }
}


// ****************************************************************************
//  Method: avtMIRvfExpression::ModifyContract
//
//  Purpose:
//      This routine allows the filter to change the data specification.
//      Tell the database that it must do MIR so that we can calculate the
//      volume fractions.
//
//  Programmer: Hank Childs
//  Creation:   December 31, 2004
//
// ****************************************************************************

avtContract_p
avtMIRvfExpression::ModifyContract(avtContract_p spec)
{
    avtSILRestriction_p silr = spec->GetDataRequest()->GetRestriction();
    avtSILRestrictionTraverser trav(silr);
    if (!trav.UsesAllMaterials())
    {
        if (!issuedWarning)
        {
           std::string warningString = "This expression can only be calculated"
                    " if no material selection has been applied.  Results may"
                    " be misleading.  Please try again with all materials on.";
           avtCallback::IssueWarning(warningString.c_str());
           issuedWarning = true;
        }
    } 

    spec->GetDataRequest()->ForceMaterialInterfaceReconstructionOn();
    return spec;
}


// ****************************************************************************
//  Method: avtMIRvfExpression::UpdateDataObjectInfo
//
//  Purpose:
//      Indicates the zones no longer correspond to the original problem.
//
//  Programmer: Hank Childs
//  Creation:   January 1, 2005
//
// ****************************************************************************

void
avtMIRvfExpression::UpdateDataObjectInfo(void)
{
    avtSingleInputExpressionFilter::UpdateDataObjectInfo();
    GetOutput()->GetInfo().GetValidity().InvalidateZones();
}


