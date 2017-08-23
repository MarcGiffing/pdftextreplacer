package com.giffing.pdf.textreplacer;

import java.io.File;
import java.io.StringWriter;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.pdmodel.PDPage;
import org.apache.pdfbox.pdmodel.PDPageContentStream;
import org.apache.pdfbox.pdmodel.PDPageContentStream.AppendMode;
import org.apache.pdfbox.pdmodel.graphics.image.PDImageXObject;
import org.apache.pdfbox.text.TextPosition;
import org.apache.pdfbox.util.Matrix;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.giffing.pdf.textreplacer.exceptions.MissingPDFException;
import com.giffing.pdf.textreplacer.exceptions.TextToReplaceNotFoundException;
import com.giffing.pdf.textreplacer.pdfutils.TextPositionFinder;

@SpringBootApplication
@EnableConfigurationProperties(TextReplacerApplicationProperties.class)
public class TextReplacerApplication implements CommandLineRunner {

	Logger logger = LoggerFactory.getLogger(TextReplacerApplication.class);
	
	@Autowired
	private TextReplacerApplicationProperties properties;

	public static void main(String[] args) {
		SpringApplication.run(TextReplacerApplication.class, args);
	}

	@Override
	public void run(String... arg0) throws Exception {

		logger.info(properties.toString());

		File pdfFile = new File(properties.getPdf());

		if (!pdfFile.exists()) {
			throw new MissingPDFException(properties.getPdf());
		}

		try (PDDocument document = PDDocument.load(pdfFile)) {

			PDPage page = document.getPage(properties.getPage());

			TextPositionFinder textPositionFinder = new TextPositionFinder(properties.getSearchText());
			textPositionFinder.setSortByPosition(true);
			textPositionFinder.setStartPage(properties.getPage()+1);
			textPositionFinder.setEndPage(properties.getPage()+1);
			textPositionFinder.writeText(document, new StringWriter());


			if (!textPositionFinder.textExists()) {
				throw new TextToReplaceNotFoundException(properties);
			}
			TextPosition textPosition = textPositionFinder.getTextPosition();


				try (PDPageContentStream contentStream = new PDPageContentStream(document, page, AppendMode.APPEND,
						true, true)) {
					PDImageXObject pdImage = PDImageXObject.createFromFile(properties.getImage(), document);
					Matrix textMatrix = textPosition.getTextMatrix();
					contentStream.drawImage(pdImage, textMatrix.getTranslateX(), textMatrix.getTranslateY() - properties.getImageHeight() + (textPosition.getHeight() *2)  , properties.getImageWidth(), properties.getImageHeight());
				}


			String targetPDFFile = properties.getTargetPDF();
			if (targetPDFFile == null) {
				targetPDFFile = properties.getPdf();
			}

			document.save(targetPDFFile);
		}

	}

}
