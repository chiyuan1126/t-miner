package technology.tabula.pipeline;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;

import javax.imageio.ImageIO;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.rendering.ImageType;

import technology.tabula.Utils;

public class TestConvert2Image {

	public static void main(String[] args) throws IOException {
		// TODO Auto-generated method stub
		PDDocument document = PDDocument.load(new File("/Users/cy2465/Downloads/testnewpdf/Y98-1025.pdf"));
		for(int i=0;i<5;i++) {
			PDPage p = document.getPage(i);
	        BufferedImage image = Utils.pageConvertToImage(document, p, 105, ImageType.RGB);
	        ImageIO.write(image, "png", new File("/Users/cy2465/Downloads/testconvertimg/"+i+".png"));
		}
		document.close();
	}

}
