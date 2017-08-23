package com.giffing.pdf.textreplacer.pdfutils;

import java.io.IOException;
import java.util.List;

import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.pdfbox.text.TextPosition;

/**
 * This class
 *
 */
public class TextPositionFinder extends PDFTextStripper {
	private String searchText;

	public String current = "";

	/**
	 * The resulting text position if text is found in pdf
	 */
	public TextPosition textPosition;

	public boolean found = false;

	/**
	 * Instantiate a new PDFTextStripper object.
	 */
	public TextPositionFinder(String searchText) throws IOException {
		this.searchText = searchText;
	}

	/**
	 * Override the default functionality of PDFTextStripper.
	 */
	@Override
	protected void writeString(String string, List<TextPosition> textPositions) throws IOException {
		if (!found) {
			for (TextPosition textPosition : textPositions) {
				current += textPosition.getUnicode();
				if (searchText.startsWith(current)) {
					if (searchText.equals(current)) {
						found = true;
					}
				} else {
					current = "";
				}

				if (current.length() == 1) {
					this.textPosition = textPosition;
				}

			}
		}
	}

	public TextPosition getTextPosition() {
		return textPosition;
	}

	public void setTextPosition(TextPosition textPosition) {
		this.textPosition = textPosition;
	}

	public boolean textExists() {
		return found;
	}

}