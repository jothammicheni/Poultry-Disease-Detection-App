package com.example.layersdiseasedetection.data;

public class Medication {

    private String[] newcastle;
    private String[] salmonella;
    private String[] coccodiocis;

    public Medication() {
        newcastle = new String[]{"Newcastle drug",
                "The results variable is being used without being defined in the provided code snippet. It seems like it should be defined somewhere else in the code.\n",
                "R.mipmap.testimage_foreground"};
        salmonella = new String[]{"Salmonella Name", "Salmonella Description", "R.mipmap.salmonella_image"};
        coccodiocis = new String[]{"SULFADIMETHOXINE",
                "Sulfadimethoxine is an antimicrobial medication commonly used in poultry farming to treat and prevent coccidiosis, a parasitic disease that affects the intestinal tract of birds. It belongs to the class of drugs known as sulfonamides, which work by inhibiting the growth of coccidia parasites. By administering sulfadimethoxine to poultry, farmers can help control coccidiosis outbreaks and promote the health and well-being of their birds. It is important to follow proper dosage instructions and veterinary guidance when using sulfadimethoxine in poultry",
                "R.mipmap.coccodiocis_image"};
    }

    public String[] getNewcastle() {
        return newcastle;
    }

    public String[] getSalmonella() {
        return salmonella;
    }

    public String[] getCoccodiocis() {
        return coccodiocis;
    }
}
