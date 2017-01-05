public class Poach{
    private int itemType;
    private int restNum = 0;
    private char letter;

    //アイテムを入れる場合
    Poach(int itemType, int itemNum){
        this.itemType = itemType;
        this.restNum = itemNum;
    }

    //文字を入れる場合
    Poach(char letter){
        this.letter = letter;
    }

    public void setRestNum(int num){
        restNum = restNum+num;
    }

    public int getRestNum(){
        return restNum;
    }

    public int getItemType(){
        return itemType;
    }

    public String getLetter(){
        return String.valueOf(letter);
    }

    public String toString(){
        String str = null;
        if(itemType == ItemController.ITEM_HAMMER){
            str = "Hammer x"+restNum;
        }else if(itemType == ItemController.ITEM_BANANA){
            str = "Banana x"+restNum;
        }else if(itemType == ItemController.ITEM_ASIATO){
            str = "DeleteAsiato x"+restNum;
        }else if(itemType == ItemController.ITEM_TIME){
            str = "Timer+";
        }else{
            str = "None";
        }
        return str;
    }
}
