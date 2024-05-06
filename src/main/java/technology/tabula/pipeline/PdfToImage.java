package technology.tabula.pipeline;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import javax.imageio.ImageIO;

import org.apache.commons.cli.ParseException;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.rendering.ImageType;
import org.apache.pdfbox.rendering.PDFRenderer;

public class PdfToImage {
    // 经过测试，dpi 为 96,100,105,120,150,200 中，105 显示效果较为清晰，体积稳定，dpi 越高图片体积越大，一般电脑显示分辨率为 96
    public static final float DEFAULT_DPI = 105;//105
    public static final String DEFAULT_IMAGE_FORMAT = ".jpg";
    
    public static void main(String[] args) throws IOException, ParseException {
    	convertDirImg(args[0],args[1]);
    	
    	/*
    	String inputdir="/Users/cy2465/Downloads/testnewpdfimg/";
    	String outputdir="/Users/cy2465/Downloads/testnewpdfimg2/";
    	File dir=new File(inputdir);
    	File[] fs=dir.listFiles();
    	for(File f:fs) {
    		System.out.println(f.getAbsolutePath());
    		if(f.isDirectory()) {
    			File[] imgs=f.listFiles();
    			String parentstr=f.getName();
    			for(File img:imgs) {
    				System.out.println(img.getAbsolutePath());
    				String substr=img.getName();
    				System.out.println(parentstr+"-"+substr);
    				File dest=new File(outputdir+parentstr+"-"+substr);
    				copyFileUsingStream(img,dest);
    			}
    		}
    	}
    	*/
    	
    }
    
    public static void copyFileUsingStream(File source, File dest) throws IOException {
        InputStream is = null;
        OutputStream os = null;
        try {
            is = new FileInputStream(source);
            os = new FileOutputStream(dest);
            byte[] buffer = new byte[1024];
            int length;
            while ((length = is.read(buffer)) > 0) {
                os.write(buffer, 0, length);
            }
        } finally {
            is.close();
            os.close();
        }
    }


	private static void convertDirImg(String sourcedir,String targetdir) {
		try {
    		File dir=new File(sourcedir);//expfiles  randomfiles //
    		File[] files=dir.listFiles();
    		int i=0;
    		for(File f:files) {
    			System.out.println(f.getName());
    			String dirpath=targetdir+f.getName().substring(0,f.getName().length()-4)+"/";
    			File tdir=new File(dirpath);
    			tdir.mkdir();
    			try {
    				pdfToImageOnePageOnImage(f.getAbsolutePath(),dirpath,20);
    				//System.out.println("delete : "+f.delete());
    			}catch(Exception ex) {
    				System.out.println("ERROR!");
    				//System.out.println("delete : "+tdir.delete());
    			}
    			i++;
    		}
    		System.out.println(files.length);
			//pdfToImageOnePageOnImage(sf.getAbsolutePath(),"/Users/cy2465/Documents/testgenimg/"+folder+"/",15);
    	} catch (Exception e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

    /**
     * pdf 转图片，只生成一张图片
     * @param pdfPath PDF路径
     * @return pdf 页数
     */
    public static int pdfToImageAllToSingleImage(String pdfPath, String imageFilePath, int pageSize) throws Exception {
        BufferedImage imageResult;
        PDDocument pdDocument = PDDocument.load(new File(pdfPath));
        PDFRenderer pdfRenderer = new PDFRenderer(pdDocument);

        int pages = pdDocument.getNumberOfPages();
        int len = Math.min(pages, pageSize);
        int width = 0;
        int imageHeightTotal = 0;

        for (int i = 0; i < len; i++) {
            BufferedImage image = pdfRenderer.renderImageWithDPI(i, DEFAULT_DPI, ImageType.RGB);
            imageHeightTotal += image.getHeight();

            if (i == 0) {
                width = image.getWidth();
            }
        }

        imageResult = new BufferedImage(width, imageHeightTotal, BufferedImage.TYPE_INT_RGB);
        int shiftHeight = 0;
        int[] singleImgRGB;

        for (int i = 0; i < len; i++) {
            BufferedImage image = pdfRenderer.renderImageWithDPI(i, DEFAULT_DPI, ImageType.RGB);
            int imageHeight = image.getHeight();

            // 计算偏移高度
            if (i > 0) {
                shiftHeight += pdfRenderer.renderImageWithDPI(i - 1, DEFAULT_DPI, ImageType.RGB).getHeight();
            }

            singleImgRGB = image.getRGB(0, 0, width, imageHeight, null, 0, width);
            // 写入流中
            imageResult.setRGB(0, shiftHeight, width, imageHeight, singleImgRGB, 0, width);
        }

        // 写图片
        ImageIO.write(imageResult, DEFAULT_IMAGE_FORMAT.replace(".", ""), new File(imageFilePath));
        pdDocument.close();
        return pages;
    }

    /**
     * pdf 转图片，每页生成一张图片
     * @param pdfPath PDF路径
     * @return pdf 页数
     */
    public static int pdfToImageOnePageOnImage(String pdfPath, String imageFilePath, int pageSize) throws Exception {
        BufferedImage imageResult;
        PDDocument pdDocument = PDDocument.load(new File(pdfPath));
        PDFRenderer pdfRenderer = new PDFRenderer(pdDocument);

        int pages = pdDocument.getNumberOfPages();
        int len = Math.min(pages, pageSize);
        int width;

        int shiftHeight = 0;
        int[] singleImgRGB;
        for (int i =0 ; i < len; i++) {
            BufferedImage image = pdfRenderer.renderImageWithDPI(i, DEFAULT_DPI, ImageType.RGB);
            width = image.getWidth();
            int imageHeight = image.getHeight();
            imageResult = new BufferedImage(width, imageHeight, BufferedImage.TYPE_INT_RGB);
            singleImgRGB = image.getRGB(0, 0, width, imageHeight, null, 0, width);
            // 写入流中
            imageResult.setRGB(0, shiftHeight, width, imageHeight, singleImgRGB, 0, width);
            // 写图片
            ImageIO.write(imageResult, DEFAULT_IMAGE_FORMAT.replace(".", ""), new File(imageFilePath + i + DEFAULT_IMAGE_FORMAT));
        }

        pdDocument.close();
        return pages;
    }
}
