// Copyright (c) Lawrence Livermore National Security, LLC and other VisIt
// Project developers.  See the top-level LICENSE file for dates and other
// details.  No copyright assignment is required to contribute to VisIt.

package llnl.visit.plots;

import llnl.visit.AttributeSubject;
import llnl.visit.CommunicationBuffer;
import llnl.visit.Plugin;
import llnl.visit.ColorAttribute;
import llnl.visit.ColorAttributeList;
import java.util.Vector;

// ****************************************************************************
// Class: BoundaryAttributes
//
// Purpose:
//    This class contains the plot attributes for the boundary plot.
//
// Notes:      Autogenerated by xml2java.
//
// Programmer: xml2java
// Creation:   omitted
//
// Modifications:
//
// ****************************************************************************

public class BoundaryAttributes extends AttributeSubject implements Plugin
{
    private static int BoundaryAttributes_numAdditionalAtts = 11;

    // Enum values
    public final static int COLORINGMETHOD_COLORBYSINGLECOLOR = 0;
    public final static int COLORINGMETHOD_COLORBYMULTIPLECOLORS = 1;
    public final static int COLORINGMETHOD_COLORBYCOLORTABLE = 2;


    public BoundaryAttributes()
    {
        super(BoundaryAttributes_numAdditionalAtts);

        colorType = COLORINGMETHOD_COLORBYMULTIPLECOLORS;
        colorTableName = new String("Default");
        invertColorTable = false;
        legendFlag = true;
        lineWidth = 0;
        singleColor = new ColorAttribute();
        multiColor = new ColorAttributeList();
        boundaryNames = new Vector();
        opacity = 1;
        wireframe = false;
        smoothingLevel = 0;
    }

    public BoundaryAttributes(int nMoreFields)
    {
        super(BoundaryAttributes_numAdditionalAtts + nMoreFields);

        colorType = COLORINGMETHOD_COLORBYMULTIPLECOLORS;
        colorTableName = new String("Default");
        invertColorTable = false;
        legendFlag = true;
        lineWidth = 0;
        singleColor = new ColorAttribute();
        multiColor = new ColorAttributeList();
        boundaryNames = new Vector();
        opacity = 1;
        wireframe = false;
        smoothingLevel = 0;
    }

    public BoundaryAttributes(BoundaryAttributes obj)
    {
        super(obj);

        int i;

        colorType = obj.colorType;
        colorTableName = new String(obj.colorTableName);
        invertColorTable = obj.invertColorTable;
        legendFlag = obj.legendFlag;
        lineWidth = obj.lineWidth;
        singleColor = new ColorAttribute(obj.singleColor);
        multiColor = new ColorAttributeList(obj.multiColor);
        boundaryNames = new Vector(obj.boundaryNames.size());
        for(i = 0; i < obj.boundaryNames.size(); ++i)
            boundaryNames.addElement(new String((String)obj.boundaryNames.elementAt(i)));

        opacity = obj.opacity;
        wireframe = obj.wireframe;
        smoothingLevel = obj.smoothingLevel;

        SelectAll();
    }

    public int Offset()
    {
        return super.Offset() + super.GetNumAdditionalAttributes();
    }

    public int GetNumAdditionalAttributes()
    {
        return BoundaryAttributes_numAdditionalAtts;
    }

    public boolean equals(BoundaryAttributes obj)
    {
        int i;

        // Compare the elements in the boundaryNames vector.
        boolean boundaryNames_equal = (obj.boundaryNames.size() == boundaryNames.size());
        for(i = 0; (i < boundaryNames.size()) && boundaryNames_equal; ++i)
        {
            // Make references to String from Object.
            String boundaryNames1 = (String)boundaryNames.elementAt(i);
            String boundaryNames2 = (String)obj.boundaryNames.elementAt(i);
            boundaryNames_equal = boundaryNames1.equals(boundaryNames2);
        }
        // Create the return value
        return ((colorType == obj.colorType) &&
                (colorTableName.equals(obj.colorTableName)) &&
                (invertColorTable == obj.invertColorTable) &&
                (legendFlag == obj.legendFlag) &&
                (lineWidth == obj.lineWidth) &&
                (singleColor == obj.singleColor) &&
                (multiColor.equals(obj.multiColor)) &&
                boundaryNames_equal &&
                (opacity == obj.opacity) &&
                (wireframe == obj.wireframe) &&
                (smoothingLevel == obj.smoothingLevel));
    }

    public String GetName() { return "Boundary"; }
    public String GetVersion() { return "1.0"; }

    // Property setting methods
    public void SetColorType(int colorType_)
    {
        colorType = colorType_;
        Select(0);
    }

    public void SetColorTableName(String colorTableName_)
    {
        colorTableName = colorTableName_;
        Select(1);
    }

    public void SetInvertColorTable(boolean invertColorTable_)
    {
        invertColorTable = invertColorTable_;
        Select(2);
    }

    public void SetLegendFlag(boolean legendFlag_)
    {
        legendFlag = legendFlag_;
        Select(3);
    }

    public void SetLineWidth(int lineWidth_)
    {
        lineWidth = lineWidth_;
        Select(4);
    }

    public void SetSingleColor(ColorAttribute singleColor_)
    {
        singleColor = singleColor_;
        Select(5);
    }

    public void SetMultiColor(ColorAttributeList multiColor_)
    {
        multiColor = multiColor_;
        Select(6);
    }

    public void SetBoundaryNames(Vector boundaryNames_)
    {
        boundaryNames = boundaryNames_;
        Select(7);
    }

    public void SetOpacity(double opacity_)
    {
        opacity = opacity_;
        Select(8);
    }

    public void SetWireframe(boolean wireframe_)
    {
        wireframe = wireframe_;
        Select(9);
    }

    public void SetSmoothingLevel(int smoothingLevel_)
    {
        smoothingLevel = smoothingLevel_;
        Select(10);
    }

    // Property getting methods
    public int                GetColorType() { return colorType; }
    public String             GetColorTableName() { return colorTableName; }
    public boolean            GetInvertColorTable() { return invertColorTable; }
    public boolean            GetLegendFlag() { return legendFlag; }
    public int                GetLineWidth() { return lineWidth; }
    public ColorAttribute     GetSingleColor() { return singleColor; }
    public ColorAttributeList GetMultiColor() { return multiColor; }
    public Vector             GetBoundaryNames() { return boundaryNames; }
    public double             GetOpacity() { return opacity; }
    public boolean            GetWireframe() { return wireframe; }
    public int                GetSmoothingLevel() { return smoothingLevel; }

    // Write and read methods.
    public void WriteAtts(CommunicationBuffer buf)
    {
        if(WriteSelect(0, buf))
            buf.WriteInt(colorType);
        if(WriteSelect(1, buf))
            buf.WriteString(colorTableName);
        if(WriteSelect(2, buf))
            buf.WriteBool(invertColorTable);
        if(WriteSelect(3, buf))
            buf.WriteBool(legendFlag);
        if(WriteSelect(4, buf))
            buf.WriteInt(lineWidth);
        if(WriteSelect(5, buf))
            singleColor.Write(buf);
        if(WriteSelect(6, buf))
            multiColor.Write(buf);
        if(WriteSelect(7, buf))
            buf.WriteStringVector(boundaryNames);
        if(WriteSelect(8, buf))
            buf.WriteDouble(opacity);
        if(WriteSelect(9, buf))
            buf.WriteBool(wireframe);
        if(WriteSelect(10, buf))
            buf.WriteInt(smoothingLevel);
    }

    public void ReadAtts(int index, CommunicationBuffer buf)
    {
        switch(index)
        {
        case 0:
            SetColorType(buf.ReadInt());
            break;
        case 1:
            SetColorTableName(buf.ReadString());
            break;
        case 2:
            SetInvertColorTable(buf.ReadBool());
            break;
        case 3:
            SetLegendFlag(buf.ReadBool());
            break;
        case 4:
            SetLineWidth(buf.ReadInt());
            break;
        case 5:
            singleColor.Read(buf);
            Select(5);
            break;
        case 6:
            multiColor.Read(buf);
            Select(6);
            break;
        case 7:
            SetBoundaryNames(buf.ReadStringVector());
            break;
        case 8:
            SetOpacity(buf.ReadDouble());
            break;
        case 9:
            SetWireframe(buf.ReadBool());
            break;
        case 10:
            SetSmoothingLevel(buf.ReadInt());
            break;
        }
    }

    public String toString(String indent)
    {
        String str = new String();
        str = str + indent + "colorType = ";
        if(colorType == COLORINGMETHOD_COLORBYSINGLECOLOR)
            str = str + "COLORINGMETHOD_COLORBYSINGLECOLOR";
        if(colorType == COLORINGMETHOD_COLORBYMULTIPLECOLORS)
            str = str + "COLORINGMETHOD_COLORBYMULTIPLECOLORS";
        if(colorType == COLORINGMETHOD_COLORBYCOLORTABLE)
            str = str + "COLORINGMETHOD_COLORBYCOLORTABLE";
        str = str + "\n";
        str = str + stringToString("colorTableName", colorTableName, indent) + "\n";
        str = str + boolToString("invertColorTable", invertColorTable, indent) + "\n";
        str = str + boolToString("legendFlag", legendFlag, indent) + "\n";
        str = str + intToString("lineWidth", lineWidth, indent) + "\n";
        str = str + indent + "singleColor = {" + singleColor.Red() + ", " + singleColor.Green() + ", " + singleColor.Blue() + ", " + singleColor.Alpha() + "}\n";
        str = str + indent + "multiColor = {\n" + multiColor.toString(indent + "    ") + indent + "}\n";
        str = str + stringVectorToString("boundaryNames", boundaryNames, indent) + "\n";
        str = str + doubleToString("opacity", opacity, indent) + "\n";
        str = str + boolToString("wireframe", wireframe, indent) + "\n";
        str = str + intToString("smoothingLevel", smoothingLevel, indent) + "\n";
        return str;
    }


    // Attributes
    private int                colorType;
    private String             colorTableName;
    private boolean            invertColorTable;
    private boolean            legendFlag;
    private int                lineWidth;
    private ColorAttribute     singleColor;
    private ColorAttributeList multiColor;
    private Vector             boundaryNames; // vector of String objects
    private double             opacity;
    private boolean            wireframe;
    private int                smoothingLevel;
}

