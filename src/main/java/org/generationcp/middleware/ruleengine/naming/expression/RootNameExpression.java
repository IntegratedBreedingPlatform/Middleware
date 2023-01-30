/**
 * Snametype: is null or contains a name type id number from the UDFLDS table. See below for some example Snametypes. If not null and if the
 * germplasm being advanced has a name of the specified type, then this is used as the root name of the advanced strain otherwise the
 * preferred name of the source is used. If the root name is a cross string (contains one or more /s not enclosed within the range of a pair
 * of parentheses) then enclose the root name in parentheses.
 */

package org.generationcp.middleware.ruleengine.naming.expression;

import org.generationcp.middleware.domain.germplasm.BasicNameDTO;
import org.generationcp.middleware.manager.GermplasmNameType;
import org.generationcp.middleware.ruleengine.pojo.AdvancingSource;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class RootNameExpression implements Expression {

	@Override
	public void apply(final List<StringBuilder> values, final AdvancingSource advancingSource, final String capturedText) {
		for (final StringBuilder value : values) {
			final Integer snametype = advancingSource.getBreedingMethod().getSnametype();
			BasicNameDTO name = null;
			final List<BasicNameDTO> names = advancingSource.getNames();

			// this checks the matching sname type of the method to the names
			if (snametype != null) {
				name = this.findNameUsingNameType(snametype, names);
			}
			// this checks the names with nstat == 1
			if (name == null) {
				// if no sname type defined or if no name found that matched the snametype
				name = this.findPreferredName(names);
			}
			// this checks the type id equal to 5
			if (name == null) {
				name = this.findNameUsingNameType(GermplasmNameType.DERIVATIVE_NAME.getUserDefinedFieldID(), names);
			}
			String nameString = "";
			// the default is type id 5
			Integer typeId = GermplasmNameType.DERIVATIVE_NAME.getUserDefinedFieldID();
			// if the snametype for the method is not null, we use the method sname type
			if (snametype != null) {
				typeId = snametype;
			}
			// if we found a matching name, we use the name type id instead
			if (name != null) {
				nameString = name.getNval();
				typeId = name.getTypeId();
			}

			advancingSource.setRootNameType(typeId);
			advancingSource.setRootName(nameString);

			if (!this.checkNameIfEnclosed(nameString)) {
				value.append("(").append(nameString).append(")");
			} else {
				value.append(nameString);
			}
		}
	}

	public BasicNameDTO findNameUsingNameType(final Integer nameType, final List<BasicNameDTO> names) {
		if (names == null) {
			return null;
		}

		for (final BasicNameDTO name : names) {
			if (name.getTypeId() != null && name.getTypeId().equals(nameType)) {
				return name;
			}
		}

		return null;
	}

	public BasicNameDTO findPreferredName(final List<BasicNameDTO> names) {
		if (names == null) {
			return null;
		}

		for (final BasicNameDTO name : names) {
			if (name.getNstat() != null && name.getNstat().equals(1)) {
				return name;
			}
		}

		return null;
	}

	@Override
	public String getExpressionKey() {
		return null;
	}

	private boolean checkNameIfEnclosed(final String name) {
		int index = name.indexOf("/", 0);
		while (index > -1 && index < name.length()) {
			if (!this.checkIfEnclosed(name, index)) {
				return false;
			}

			index = name.indexOf("/", index + 1);
		}
		return true;
	}

	private boolean checkIfEnclosed(final String name, final int index) {

		return this.checkNeighbor(name, index, '(', -1, 0, ')') && this.checkNeighbor(name, index, ')', 1, name.length() - 1, '(');
	}

	private boolean checkNeighbor(final String name, final int index, final char literal, final int delta, final int stopPoint,
		final char oppositeLiteral) {
		int oppositeCount = 0;
		for (int i = index + delta; i != stopPoint + delta; i = i + delta) {
			if (name.charAt(i) == literal) {
				if (oppositeCount == 0) {
					return true;
				} else {
					oppositeCount--;
				}
			} else if (name.charAt(i) == oppositeLiteral) {
				oppositeCount++;
			}
		}
		return false;
	}
}
