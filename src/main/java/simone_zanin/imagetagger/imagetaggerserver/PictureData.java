package simone_zanin.imagetagger.imagetaggerserver;

public class PictureData {
    public String[] tags;
    public String description, imagePath, imageBase64;
    public double latitude, longitude;
    public int uploadId;

    @Override
    public String toString() {
        StringBuilder sb = new StringBuilder();
        sb.append("\nimagePath:\t");
        sb.append(imagePath);
        sb.append("\nB64 image length:\t");
        sb.append(imageBase64.length());
        sb.append("\nDescription:\t");
        sb.append(description);
        sb.append("\nCoords:\nLAT\t");
        sb.append(latitude);
        sb.append("\tLON\t");
        sb.append(longitude);
        sb.append("\nTags:\n");
        for (String s:tags) {
            sb.append("- ");
            sb.append(s);
            sb.append("\n");
        }
        return sb.toString();
    }
    
    
}
