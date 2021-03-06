package sgdk.rescomp.resource.internal;

import java.io.ByteArrayOutputStream;
import java.io.IOException;

import sgdk.rescomp.Resource;
import sgdk.rescomp.tool.Util;
import sgdk.rescomp.type.SpriteCell;

public class VDPSprite extends Resource
{
    public final int offsetY;
    public final int offsetX;
    public final int wt;
    public final int ht;

    final int hc;

    public VDPSprite(String id, int offX, int offY, int w, int h)
    {
        super(id);

        if ((offX < 0) || (offX > 255) || (offY < 0) || (offY > 255))
            throw new IllegalArgumentException(
                    "Error: sprite '" + id + "' offset X / Y is out of range (< 0 or > 255)");
        // if ((offX < -128) || (offX > 127) || (offY < -128) || (offY > 127))
        // throw new IllegalArgumentException(
        // "Error: sprite '" + id + "' offset X / Y is out of range (< -128 or > 127)");

        this.offsetX = offX;
        this.offsetY = offY;
        this.wt = w;
        this.ht = h;

        // compute hash code
        hc = (offsetX << 0) ^ (offsetY << 8) ^ (wt << 16) ^ (ht << 24);
    }

    public VDPSprite(String id, SpriteCell sprite)
    {
        this(id, sprite.x, sprite.y, sprite.width / 8, sprite.height / 8);
    }

    public int getFormattedSize()
    {
        return ((wt - 1) << 2) | (ht - 1);
    }

    @Override
    public int internalHashCode()
    {
        return hc;
    }

    @Override
    public boolean internalEquals(Object obj)
    {
        if (obj instanceof VDPSprite)
        {
            final VDPSprite vdpSprite = (VDPSprite) obj;
            return (offsetX == vdpSprite.offsetX) && (offsetY == vdpSprite.offsetY) && (wt == vdpSprite.wt)
                    && (ht == vdpSprite.ht);
        }

        return false;
    }

    @Override
    public String toString()
    {
        return id + ": [" + offsetX + "," + offsetY + "-" + (wt * 8) + "," + (ht * 8) + "]";
    }

    @Override
    public int shallowSize()
    {
        return 4;
    }

    @Override
    public int totalSize()
    {
        return shallowSize();
    }

    @Override
    public void out(ByteArrayOutputStream outB, StringBuilder outS, StringBuilder outH) throws IOException
    {
        // FrameVDPSprite structure
        Util.decl(outS, outH, "FrameVDPSprite", id, 2, global);

        // respect VDP sprite field order: (numTile, offsetY, size, offsetX)
        outS.append("    dc.w    " + (((ht * wt) << 8) | ((offsetY << 0) & 0xFF)) + "\n");
        outS.append("    dc.w    " + ((getFormattedSize() << 8) | ((offsetX << 0) & 0xFF)) + "\n");
        // write to binary buffer
        outB.write(ht * wt);
        outB.write(offsetY);
        outB.write(getFormattedSize());
        outB.write(offsetX);

        outS.append("\n");
    }
}
