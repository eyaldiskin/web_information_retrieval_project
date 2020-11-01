
public class DictTrieData {
    private Double[] vec;
    private Boolean isWord;

    /**
     * @return the vec
     */
    public Double[] getVec() {
        return vec;
    }
    /**
     * @param vec the vec to set
     */
    public void setVec(Double[] vec) {
        this.vec = vec;
    }

    /**
     * @return the isWord
     */
    public Boolean getIsWord() {
        return isWord;
    }


    public DictTrieData(Boolean isWord) {
        this.vec = null;
        this.isWord = isWord;
    }

}