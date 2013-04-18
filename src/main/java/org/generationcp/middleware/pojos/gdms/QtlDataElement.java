package org.generationcp.middleware.pojos.gdms;

/**
 * Mainly used by GenotypicDataManager.getQtlDataByTraits()
 * 
 * @author Joyce Avestro
 *
 */
public class QtlDataElement {

	String qtlName;
	String linkageGroup;
	Float position; 
	Float minPosition;
	Float maxPosition; 
	String trait; 
	String experiment; 
	String leftFlankingMarker; 
	String rightFlankingMarker; 
	Integer effect; 
	Float scoreValue; 
	Float rSquare;

	
	public QtlDataElement() {
	}


	public QtlDataElement(String qtlName, String linkageGroup, Float position,
			Float minPosition, Float maxPosition, String trait,
			String experiment, String leftFlankingMarker,
			String rightFlankingMarker, Integer effect, Float scoreValue,
			Float rSquare) {
		super();
		this.qtlName = qtlName;
		this.linkageGroup = linkageGroup;
		this.position = position;
		this.minPosition = minPosition;
		this.maxPosition = maxPosition;
		this.trait = trait;
		this.experiment = experiment;
		this.leftFlankingMarker = leftFlankingMarker;
		this.rightFlankingMarker = rightFlankingMarker;
		this.effect = effect;
		this.scoreValue = scoreValue;
		this.rSquare = rSquare;
	}


	public String getQtlName() {
		return qtlName;
	}


	public void setQtlName(String qtlName) {
		this.qtlName = qtlName;
	}


	public String getLinkageGroup() {
		return linkageGroup;
	}


	public void setLinkageGroup(String linkageGroup) {
		this.linkageGroup = linkageGroup;
	}


	public Float getPosition() {
		return position;
	}


	public void setPosition(Float position) {
		this.position = position;
	}


	public Float getMinPosition() {
		return minPosition;
	}


	public void setMinPosition(Float minPosition) {
		this.minPosition = minPosition;
	}


	public Float getMaxPosition() {
		return maxPosition;
	}


	public void setMaxPosition(Float maxPosition) {
		this.maxPosition = maxPosition;
	}


	public String getTrait() {
		return trait;
	}


	public void setTrait(String trait) {
		this.trait = trait;
	}


	public String getExperiment() {
		return experiment;
	}


	public void setExperiment(String experiment) {
		this.experiment = experiment;
	}


	public String getLeftFlankingMarker() {
		return leftFlankingMarker;
	}


	public void setLeftFlankingMarker(String leftFlankingMarker) {
		this.leftFlankingMarker = leftFlankingMarker;
	}


	public String getRightFlankingMarker() {
		return rightFlankingMarker;
	}


	public void setRightFlankingMarker(String rightFlankingMarker) {
		this.rightFlankingMarker = rightFlankingMarker;
	}


	public Integer getEffect() {
		return effect;
	}


	public void setEffect(Integer effect) {
		this.effect = effect;
	}


	public Float getScoreValue() {
		return scoreValue;
	}


	public void setScoreValue(Float scoreValue) {
		this.scoreValue = scoreValue;
	}


	public Float getrSquare() {
		return rSquare;
	}


	public void setrSquare(Float rSquare) {
		this.rSquare = rSquare;
	}


	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("QtlData [qtlName=");
		builder.append(qtlName);
		builder.append(", linkageGroup=");
		builder.append(linkageGroup);
		builder.append(", position=");
		builder.append(position);
		builder.append(", minPosition=");
		builder.append(minPosition);
		builder.append(", maxPosition=");
		builder.append(maxPosition);
		builder.append(", trait=");
		builder.append(trait);
		builder.append(", experiment=");
		builder.append(experiment);
		builder.append(", leftFlankingMarker=");
		builder.append(leftFlankingMarker);
		builder.append(", rightFlankingMarker=");
		builder.append(rightFlankingMarker);
		builder.append(", effect=");
		builder.append(effect);
		builder.append(", scoreValue=");
		builder.append(scoreValue);
		builder.append(", rSquare=");
		builder.append(rSquare);
		builder.append("]");
		return builder.toString();
	}
	
	
}
