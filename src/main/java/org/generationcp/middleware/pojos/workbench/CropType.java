package org.generationcp.middleware.pojos.workbench;

public enum CropType {
     CHICKPEA("Chickpea")
    ,COWPEA("Cowpea")
    ,MAIZE("Maize")
    ,RICE("Rice")
    ,WHEAT("Wheat")
    ,CASSAVA("Cassava")
    ;
    
    private String cropName;

    CropType(String name) {
         this.cropName = name;
     }
    
    public String getCropName() {
        return cropName;
    }
}
