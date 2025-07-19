package com.bluestaggo.genlayerpreviewer;

import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.awt.image.BufferedImage;

public class ImageTransferable implements Transferable {
    private final BufferedImage image;

    public ImageTransferable(BufferedImage image) {
        this.image = image;
    }

    @Override
	public Object getTransferData(DataFlavor flavor) throws UnsupportedFlavorException {
        if (flavor.equals(DataFlavor.imageFlavor)) {
	        return image;
        }
        throw new UnsupportedFlavorException(flavor);
    }

    @Override
	public DataFlavor[] getTransferDataFlavors() {
        return new DataFlavor[]{DataFlavor.imageFlavor};
    }

    @Override
	public boolean isDataFlavorSupported(DataFlavor flavor) {
        return flavor.equals(DataFlavor.imageFlavor);
    }
}
