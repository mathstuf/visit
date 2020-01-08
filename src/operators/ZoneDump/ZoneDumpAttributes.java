// Copyright (c) Lawrence Livermore National Security, LLC and other VisIt
// Project developers.  See the top-level LICENSE file for dates and other
// details.  No copyright assignment is required to contribute to VisIt.

package llnl.visit.operators;

import llnl.visit.AttributeSubject;
import llnl.visit.CommunicationBuffer;
import llnl.visit.Plugin;

// ****************************************************************************
// Class: ZoneDumpAttributes
//
// Purpose:
//    Zone Dump Control
//
// Notes:      Autogenerated by xml2java.
//
// Programmer: xml2java
// Creation:   omitted
//
// Modifications:
//
// ****************************************************************************

public class ZoneDumpAttributes extends AttributeSubject implements Plugin
{
    private static int ZoneDumpAttributes_numAdditionalAtts = 5;

    public ZoneDumpAttributes()
    {
        super(ZoneDumpAttributes_numAdditionalAtts);

        variable = new String("");
        lowerBound = -1e+37;
        upperBound = 1e+37;
        outputFile = new String("visit_zonedump.zod");
        enabled = false;
    }

    public ZoneDumpAttributes(int nMoreFields)
    {
        super(ZoneDumpAttributes_numAdditionalAtts + nMoreFields);

        variable = new String("");
        lowerBound = -1e+37;
        upperBound = 1e+37;
        outputFile = new String("visit_zonedump.zod");
        enabled = false;
    }

    public ZoneDumpAttributes(ZoneDumpAttributes obj)
    {
        super(obj);

        variable = new String(obj.variable);
        lowerBound = obj.lowerBound;
        upperBound = obj.upperBound;
        outputFile = new String(obj.outputFile);
        enabled = obj.enabled;

        SelectAll();
    }

    public int Offset()
    {
        return super.Offset() + super.GetNumAdditionalAttributes();
    }

    public int GetNumAdditionalAttributes()
    {
        return ZoneDumpAttributes_numAdditionalAtts;
    }

    public boolean equals(ZoneDumpAttributes obj)
    {
        // Create the return value
        return ((variable.equals(obj.variable)) &&
                (lowerBound == obj.lowerBound) &&
                (upperBound == obj.upperBound) &&
                (outputFile.equals(obj.outputFile)) &&
                (enabled == obj.enabled));
    }

    public String GetName() { return "ZoneDump"; }
    public String GetVersion() { return "1.0"; }

    // Property setting methods
    public void SetVariable(String variable_)
    {
        variable = variable_;
        Select(0);
    }

    public void SetLowerBound(double lowerBound_)
    {
        lowerBound = lowerBound_;
        Select(1);
    }

    public void SetUpperBound(double upperBound_)
    {
        upperBound = upperBound_;
        Select(2);
    }

    public void SetOutputFile(String outputFile_)
    {
        outputFile = outputFile_;
        Select(3);
    }

    public void SetEnabled(boolean enabled_)
    {
        enabled = enabled_;
        Select(4);
    }

    // Property getting methods
    public String  GetVariable() { return variable; }
    public double  GetLowerBound() { return lowerBound; }
    public double  GetUpperBound() { return upperBound; }
    public String  GetOutputFile() { return outputFile; }
    public boolean GetEnabled() { return enabled; }

    // Write and read methods.
    public void WriteAtts(CommunicationBuffer buf)
    {
        if(WriteSelect(0, buf))
            buf.WriteString(variable);
        if(WriteSelect(1, buf))
            buf.WriteDouble(lowerBound);
        if(WriteSelect(2, buf))
            buf.WriteDouble(upperBound);
        if(WriteSelect(3, buf))
            buf.WriteString(outputFile);
        if(WriteSelect(4, buf))
            buf.WriteBool(enabled);
    }

    public void ReadAtts(int index, CommunicationBuffer buf)
    {
        switch(index)
        {
        case 0:
            SetVariable(buf.ReadString());
            break;
        case 1:
            SetLowerBound(buf.ReadDouble());
            break;
        case 2:
            SetUpperBound(buf.ReadDouble());
            break;
        case 3:
            SetOutputFile(buf.ReadString());
            break;
        case 4:
            SetEnabled(buf.ReadBool());
            break;
        }
    }

    public String toString(String indent)
    {
        String str = new String();
        str = str + stringToString("variable", variable, indent) + "\n";
        str = str + doubleToString("lowerBound", lowerBound, indent) + "\n";
        str = str + doubleToString("upperBound", upperBound, indent) + "\n";
        str = str + stringToString("outputFile", outputFile, indent) + "\n";
        str = str + boolToString("enabled", enabled, indent) + "\n";
        return str;
    }


    // Attributes
    private String  variable;
    private double  lowerBound;
    private double  upperBound;
    private String  outputFile;
    private boolean enabled;
}

