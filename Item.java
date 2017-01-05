public class Item implements Cloneable{
    private int posX;
    private int posY;
    private int itemType;

    Item(int posX, int posY, int itemType){
        this.posX = posX;
        this.posY = posY;
        this.itemType = itemType;
    }

    //クローン生成
    @Override
    public Item clone(){
        Item cloneItem = null;
        try{
            cloneItem = (Item)super.clone();
        }catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return cloneItem;
    }

    public int getPosX(){
        return posX;
    }

    public int getPosY(){
        return posY;
    }

    public int getItemType(){
        return itemType;
    }
}
