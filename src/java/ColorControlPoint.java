// ***************************************************************************
//
// Copyright (c) 2000 - 2010, Lawrence Livermore National Security, LLC
// Produced at the Lawrence Livermore National Laboratory
// LLNL-CODE-442911
// All rights reserved.
//
// This file is  part of VisIt. For  details, see https://visit.llnl.gov/.  The
// full copyright notice is contained in the file COPYRIGHT located at the root
// of the VisIt distribution or at http://www.llnl.gov/visit/copyright.html.
//
// Redistribution  and  use  in  source  and  binary  forms,  with  or  without
// modification, are permitted provided that the following conditions are met:
//
//  - Redistributions of  source code must  retain the above  copyright notice,
//    this list of conditions and the disclaimer below.
//  - Redistributions in binary form must reproduce the above copyright notice,
//    this  list of  conditions  and  the  disclaimer (as noted below)  in  the
//    documentation and/or other materials provided with the distribution.
//  - Neither the name of  the LLNS/LLNL nor the names of  its contributors may
//    be used to endorse or promote products derived from this software without
//    specific prior written permission.
//
// THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT  HOLDERS AND CONTRIBUTORS "AS IS"
// AND ANY EXPRESS OR  IMPLIED WARRANTIES, INCLUDING,  BUT NOT  LIMITED TO, THE
// IMPLIED WARRANTIES OF MERCHANTABILITY AND  FITNESS FOR A PARTICULAR  PURPOSE
// ARE  DISCLAIMED. IN  NO EVENT  SHALL LAWRENCE  LIVERMORE NATIONAL  SECURITY,
// LLC, THE  U.S.  DEPARTMENT OF  ENERGY  OR  CONTRIBUTORS BE  LIABLE  FOR  ANY
// DIRECT,  INDIRECT,   INCIDENTAL,   SPECIAL,   EXEMPLARY,  OR   CONSEQUENTIAL
// DAMAGES (INCLUDING, BUT NOT  LIMITED TO, PROCUREMENT OF  SUBSTITUTE GOODS OR
// SERVICES; LOSS OF  USE, DATA, OR PROFITS; OR  BUSINESS INTERRUPTION) HOWEVER
// CAUSED  AND  ON  ANY  THEORY  OF  LIABILITY,  WHETHER  IN  CONTRACT,  STRICT
// LIABILITY, OR TORT  (INCLUDING NEGLIGENCE OR OTHERWISE)  ARISING IN ANY  WAY
// OUT OF THE  USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH
// DAMAGE.
//
// ***************************************************************************

package llnl.visit;


// ****************************************************************************
// Class: ColorControlPoint
//
// Purpose:
//    This class contains an RGBA color with a position value.
//
// Notes:      Autogenerated by xml2java.
//
// Programmer: xml2java
// Creation:   omitted
//
// Modifications:
//   
// ****************************************************************************

public class ColorControlPoint extends AttributeSubject
{
    private static int ColorControlPoint_numAdditionalAtts = 2;

    public ColorControlPoint()
    {
        super(ColorControlPoint_numAdditionalAtts);

        colors = new byte[4];
        colors[0] = (byte)0;
        colors[1] = (byte)0;
        colors[2] = (byte)0;
        colors[3] = (byte)255;
        position = 0f;
    }

    public ColorControlPoint(int nMoreFields)
    {
        super(ColorControlPoint_numAdditionalAtts + nMoreFields);

        colors = new byte[4];
        colors[0] = (byte)0;
        colors[1] = (byte)0;
        colors[2] = (byte)0;
        colors[3] = (byte)255;
        position = 0f;
    }

    public ColorControlPoint(ColorControlPoint obj)
    {
        super(ColorControlPoint_numAdditionalAtts);

        int i;

        colors = new byte[4];
        for(i = 0; i < obj.colors.length; ++i)
            colors[i] = obj.colors[i];

        position = obj.position;

        SelectAll();
    }

    public int Offset()
    {
        return super.Offset() + super.GetNumAdditionalAttributes();
    }

    public int GetNumAdditionalAttributes()
    {
        return ColorControlPoint_numAdditionalAtts;
    }

    public boolean equals(ColorControlPoint obj)
    {
        int i;

        // Compare the colors arrays.
        boolean colors_equal = true;
        for(i = 0; i < 4 && colors_equal; ++i)
            colors_equal = (colors[i] == obj.colors[i]);

        // Create the return value
        return (colors_equal &&
                (position == obj.position));
    }

    // Property setting methods
    public void SetColors(byte[] colors_)
    {
        colors[0] = colors_[0];
        colors[1] = colors_[1];
        colors[2] = colors_[2];
        colors[3] = colors_[3];
        Select(0);
    }

    public void SetColors(byte e0, byte e1, byte e2, byte e3)
    {
        colors[0] = e0;
        colors[1] = e1;
        colors[2] = e2;
        colors[3] = e3;
        Select(0);
    }

    public void SetPosition(float position_)
    {
        position = position_;
        Select(1);
    }

    // Property getting methods
    public byte[] GetColors() { return colors; }
    public float  GetPosition() { return position; }

    // Write and read methods.
    public void WriteAtts(CommunicationBuffer buf)
    {
        if(WriteSelect(0, buf))
            buf.WriteByteArray(colors, true);
        if(WriteSelect(1, buf))
            buf.WriteFloat(position);
    }

    public void ReadAtts(int index, CommunicationBuffer buf)
    {
        switch(index)
        {
        case 0:
            SetColors(buf.ReadByteArray());
            break;
        case 1:
            SetPosition(buf.ReadFloat());
            break;
        }
    }

    public String toString(String indent)
    {
        String str = new String();
        str = str + ucharArrayToString("colors", colors, indent) + "\n";
        str = str + floatToString("position", position, indent) + "\n";
        return str;
    }


    // Attributes
    private byte[] colors;
    private float  position;
}

