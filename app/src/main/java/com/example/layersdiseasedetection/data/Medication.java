package com.example.layersdiseasedetection.data;

public class Medication {

    private String[] newcastle;
    private String[] salmonella;
    private String[] coccodiocis;

    public Medication() {
        newcastle = new String[]{" LaSota Strain",
                "The Newcastle Lasota vaccine is a modified live, freeze-dried vaccine that contains the B1 type LaSota strain of the Newcastle disease virus. It is used to immunize poultry against Newcastle disease, a highly contagious and often fatal viral infection that can cause significant economic losses in the poultry industry. The vaccine is designed to stimulate the bird's immune system to produce antibodies that can protect against the disease..\n",
                "R.mipmap.testimage_foreground"};
        salmonella = new String[]{"Anti-diarrheals",
                "Your health care provider may prescribe antibiotics to kill the bacteria. These are usually given if your provider suspects that salmonella bacteria have entered your bloodstream, your infection is severe or you have a weakened immune system.\n" +
                        "\n" +
                        "Antibiotics are not helpful in most cases of salmonella infection. In fact, antibiotics may extend the period in which you carry the bacteria and can infect others. They can also increase your risk of getting infected again (relapse).",
                "R.mipmap.salmonella_image"};
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
