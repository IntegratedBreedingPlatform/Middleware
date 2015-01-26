package org.generationcp.middleware.reports;

import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;

import net.sf.jasperreports.engine.JRDataSource;
import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JasperCompileManager;
import net.sf.jasperreports.engine.JasperExportManager;
import net.sf.jasperreports.engine.JasperFillManager;
import net.sf.jasperreports.engine.JasperPrint;
import net.sf.jasperreports.engine.JasperReport;
import net.sf.jasperreports.engine.data.JRAbstractBeanDataSource;
import net.sf.jasperreports.engine.design.JRDesignBand;
import net.sf.jasperreports.engine.design.JRDesignExpression;
import net.sf.jasperreports.engine.design.JRDesignField;
import net.sf.jasperreports.engine.design.JRDesignSection;
import net.sf.jasperreports.engine.design.JRDesignStyle;
import net.sf.jasperreports.engine.design.JRDesignTextField;
import net.sf.jasperreports.engine.design.JasperDesign;
import net.sf.jasperreports.engine.type.HorizontalAlignEnum;
import net.sf.jasperreports.engine.xml.JRXmlLoader;


public abstract class AbstractDynamicReporter extends AbstractReporter {
	public static final String COL_EXPR_PREFIX = "col";
	public static final String COL_HEADER_EXPR_PREFIX = "header";
	private final static int TOTAL_PAGE_WIDTH = 800;
	 
    // The whitespace between columns in pixels
    private final static int SPACE_BETWEEN_COLS = 0;
 
    // The height in pixels of an element in a row and column
    private final static int COLUMN_HEIGHT = 12;
 
    // The total height of the column header or detail band
    private final static int BAND_HEIGHT = 12;
 
    // The left and right margin in pixels
    private final static int MARGIN = 10;

	List<String> columnHeaders = null;
    
    @SuppressWarnings("unchecked")
	@Override
	public JasperPrint buildJRPrint(Map<String, Object> args) throws JRException{
		
		String jasperFilesPath = getTemplatePath();
		Map<String, Object> jrParams = null;
		JRDataSource jrDataSource = null;
		JasperReport jasperReport = null;
		Collection<?> collectionDataSource = null;
		
		if(null != args){
			jrParams = buildJRParams(args);
			setFileName(buildOutputFileName(jrParams));

			if(args.containsKey("dataSource")){
				collectionDataSource = (Collection<?>)args.get("dataSource");
				columnHeaders = (List<String>)(args.get("columnHeaders"));
				
				jrDataSource = buildJRDataSource(collectionDataSource);
				System.out.println("datasource done!!!");
			
		        JasperDesign jasperReportDesign = JRXmlLoader.load(getTemplatePath().replace(".jasper", ".jrxml"));
			 
				//modifies the jasper design to accept the data source of unknown number of columns
				addDynamicColumns(jasperReportDesign, columnHeaders.size());
				 
				jasperReport = JasperCompileManager.compileReport(jasperReportDesign);
			}
			jrPrint = JasperFillManager.fillReport(jasperReport, jrParams, jrDataSource);
			
		}

		return jrPrint;

	}
	
	@Override
	public JRDataSource buildJRDataSource(Collection<?> dataRecords){
		
		return new DynamicColumnDataSource(columnHeaders, convertBeanCollectionToStringLists(dataRecords));
	}
	
	public abstract List<List<String>> convertBeanCollectionToStringLists(Collection<?> dataRecords);

	private void addDynamicColumns(JasperDesign jasperDesign, int numColumns) throws JRException{
		 
        JRDesignBand detailBand = new JRDesignBand();
        JRDesignBand headerBand = new JRDesignBand();
 
        JRDesignStyle normalStyle = getNormalStyle();
        JRDesignStyle columnHeaderStyle = getColumnHeaderStyle();
        jasperDesign.addStyle(normalStyle);
        jasperDesign.addStyle(columnHeaderStyle);
 
        int xPos = MARGIN;
        int columnWidth = (TOTAL_PAGE_WIDTH - (SPACE_BETWEEN_COLS * (numColumns - 1))) / numColumns;
 
        for (int i = 0; i < numColumns; i++) {
 
            // Create a Column Field
            JRDesignField field = new JRDesignField();
            field.setName(COL_EXPR_PREFIX + i);
            field.setValueClass(java.lang.String.class);
            jasperDesign.addField(field);
 
            // Create a Header Field
            JRDesignField headerField = new JRDesignField();
            headerField.setName(COL_HEADER_EXPR_PREFIX + i);
            headerField.setValueClass(java.lang.String.class);
            jasperDesign.addField(headerField);
 
            // Add a Header Field to the headerBand
            headerBand.setHeight(BAND_HEIGHT);
            JRDesignTextField colHeaderField = new JRDesignTextField();
            colHeaderField.setX(xPos);
            colHeaderField.setY(0);
            colHeaderField.setWidth(columnWidth);
            colHeaderField.setHeight(COLUMN_HEIGHT);
            colHeaderField.setHorizontalAlignment(HorizontalAlignEnum.LEFT);
            colHeaderField.setStyle(columnHeaderStyle);
            JRDesignExpression headerExpression = new JRDesignExpression();
            headerExpression.setValueClass(java.lang.String.class);
            headerExpression.setText("$F{" + COL_HEADER_EXPR_PREFIX + i + "}");
            colHeaderField.setExpression(headerExpression);
            headerBand.addElement(colHeaderField);
 
            // Add text field to the detailBand
            detailBand.setHeight(BAND_HEIGHT);
            JRDesignTextField textField = new JRDesignTextField();
            textField.setX(xPos);
            textField.setY(0);
            textField.setWidth(columnWidth);
            textField.setHeight(COLUMN_HEIGHT);
            textField.setHorizontalAlignment(HorizontalAlignEnum.LEFT);
            textField.setStyle(normalStyle);
            JRDesignExpression expression = new JRDesignExpression();
            expression.setValueClass(java.lang.String.class);
            expression.setText("$F{" + COL_EXPR_PREFIX + i + "}");
            textField.setExpression(expression);
            detailBand.addElement(textField);
 
            xPos = xPos + columnWidth + SPACE_BETWEEN_COLS;
        }
 
        jasperDesign.setColumnHeader(headerBand);
        ((JRDesignSection)jasperDesign.getDetailSection()).addBand(detailBand);

	}
	
    private JRDesignStyle getNormalStyle() {
        JRDesignStyle normalStyle = new JRDesignStyle();
        normalStyle.setName("Sans_Normal");
        normalStyle.setDefault(true);
        normalStyle.setFontName("SansSerif");
        normalStyle.setFontSize(8);
        normalStyle.setPdfFontName("Helvetica");
        normalStyle.setPdfEncoding("UTF-8");
        normalStyle.setPdfEmbedded(false);
        return normalStyle;
    }
 
    private JRDesignStyle getColumnHeaderStyle() {
        JRDesignStyle columnHeaderStyle = new JRDesignStyle();
        columnHeaderStyle.setName("Sans_Header");
        columnHeaderStyle.setDefault(false);
        columnHeaderStyle.setFontName("SansSerif");
        columnHeaderStyle.setFontSize(10);
        columnHeaderStyle.setBold(true);
        columnHeaderStyle.setPdfFontName("Helvetica");
        columnHeaderStyle.setPdfEncoding("UTF-8");
        columnHeaderStyle.setPdfEmbedded(false);
        return columnHeaderStyle;
    }
}
