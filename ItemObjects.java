import javafx.scene.image.Image;
public class ItemObjects{
    public static final int ITEM_HAMMER    = 0;
    public static final int ITEM_RED_STONE = 1;
   // public static final int ITEM_STAMP     = 2;
    private MapData mapData;
    private MoveChara chara;
    public Hammer[] hammers;
    //public Word[] keyWord;
    private Image[] itemImage;

    ItemObjects(){

    }
    
    ItemObjects(MapData mapData){
        this.mapData = mapData;
        this.chara = chara;
        itemImage = new Image[1];
        itemImage[ITEM_HAMMER] = new Image("image/HAMMER.png");
        //itemImage[ITEM_RED_STONE] = new Image("RED_OBJECT");
        //itemImage[ITEM_STAMP] = new Image("STAMP.png");
        putItem(ITEM_HAMMER,2);
        System.out.println("ItemObjects OK");
    }

    //getImageメソッド
    //parameter : itemの番号
    //return    : 選択されたitemのイメージ
    //
    public Image getImage(int itemID) {
        return itemImage[itemID];
    }

    //itemが置けるのは3(TYPE_ITEM)の場所だけ
    //noItemAtメソッド
    //paramter 
    //  アイテム座標
    //return
    //  その座標に他のアイテムが置かれてなければtrueを返す
    //  
    public boolean noItemAt(int posX, int posY){
        boolean can_put_f = false;
        for(Hammer h: hammers){
            if(h != null){
                if(h.getPosX() == posX && h.getPosY() == posY){
                    return false;
                }else{
                    can_put_f = true;
                }
            }else{
                can_put_f = true;
                break;
            }
        }  
        /*
        for(Word w: keyWord){
            if(w!=null){
                if(w.getPosX() == posX && w.getPosY() == posY){
                    return false;
                }else{
                    can_put_f = true;
                }
            }else{
                can_put_f = true;
                break;
            }
        }  
        */
        return can_put_f;
    }

    //putItemメソッド
    //parameter 
    //  int itemType : アイテムの種類
    //  int itemNum  : アイテムの個数
    //
    public void putItem(int itemType, int thisItemNum){
        int x,y;
        int cnt = 0;

        if(itemType == ITEM_HAMMER){
            hammers = new Hammer[thisItemNum];
        }else if(itemType == ITEM_RED_STONE){

        }else{

        }

        while(cnt < thisItemNum){
            mapData.choseItemPos();
            x = mapData.getItemPosX();
            y = mapData.getItemPosY();
            if(noItemAt(x,y)){
                switch(itemType){
                    case 0: //Hammer
                        hammers[cnt] = new Hammer(x,y);
                        System.out.println("Hammer created "+hammers[cnt].getPosX()+","+hammers[cnt].getPosY());
                        break;
                    case 1: //足跡つかないグッズ
                        break;

                    case 2:
                        break;

                    default:
                        break;
                }
                cnt++;
            }
        }
    }
}
