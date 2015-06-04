
package org.generationcp.middleware.reports;

import java.util.Iterator;
import java.util.List;

import net.sf.jasperreports.engine.JRException;
import net.sf.jasperreports.engine.JRField;
import net.sf.jasperreports.engine.data.JRAbstractBeanDataSource;

class DynamicColumnDataSource extends JRAbstractBeanDataSource {

	private final List<String> columnHeaders;
	private final List<List<String>> rows;
	private Iterator<List<String>> iterator;
	private List<String> currentRow;

	public DynamicColumnDataSource(List<String> columnHeaders, List<List<String>> rows) {
		super(true);

		this.rows = rows;
		this.columnHeaders = columnHeaders;

		if (this.rows != null && this.rows != null) {
			this.iterator = this.rows.iterator();
		}
	}

	@Override
	public boolean next() {
		boolean hasNext = false;

		if (this.iterator != null) {
			hasNext = this.iterator.hasNext();

			if (hasNext) {
				this.currentRow = this.iterator.next();
			}
		}

		return hasNext;
	}

	@Override
	public Object getFieldValue(JRField field) throws JRException {
		String fieldName = field.getName();
		if (fieldName.startsWith(AbstractDynamicReporter.COL_EXPR_PREFIX)) {
			String indexValue = fieldName.substring(AbstractDynamicReporter.COL_EXPR_PREFIX.length());
			String column = this.currentRow.get(Integer.parseInt(indexValue));
			return column;
		} else if (fieldName.startsWith(AbstractDynamicReporter.COL_HEADER_EXPR_PREFIX)) {
			int indexValue = Integer.parseInt(fieldName.substring(AbstractDynamicReporter.COL_HEADER_EXPR_PREFIX.length()));
			String columnHeader = this.columnHeaders.get(indexValue);
			return columnHeader;
		} else {
			throw new RuntimeException("The Jasper Report's field name [" + fieldName + "] is not valid.");
		}
	}

	@Override
	public void moveFirst() {
		if (this.rows != null) {
			this.iterator = this.rows.iterator();
		}
	}
}
