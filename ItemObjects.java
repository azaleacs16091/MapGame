import javafx.scene.image.Image;
public class ItemObjects{
    public static final int TYPE_HAMMER    = 0;
    public static final int TYPE_RED_STONE = 1;
   // public static final int TYPE_STAMP     = 2;
    private MapData mapData;
    public Hammer[] hammers;
    //public Word[] keyWord;
    private Image[] itemImage;

    ItemObjects(){

    }
    
    ItemObjects(MapData mapData){
        this.mapData = mapData;
        itemImage = new Image[1];

        itemImage[TYPE_HAMMER] = new Image("image/HAMMER.png");
        //itemImage[TYPE_RED_STONE] = new Image("RED_OBJECT");
        //itemImage[TYPE_STAMP] = new Image("STAMP.png");
        putItem(TYPE_HAMMER,2);

    }

    //getImageメソッド
    //parameter : itemの番号
    //return    : 選択されたitemのイメージ
    //
    public Image getImage(int itemID) {
        return itemImage[itemID];
    }

    //canPutItemメソッド
    //paramter 
    //  候補点
    //return
    //  候補点にアイテムを置けるならtrueを返す
    //  
    public boolean canPutItem(int posX, int posY){
        boolean canPut_f = false; 
        if (mapData.getMap(posX, posY) == MapData.TYPE_WALL){
            return false;
        } else if (mapData.getMap(posX, posY) == MapData.TYPE_NONE){
            return true;
        } else if (mapData.getMap(posX, posY) == MapData.TYPE_STONE){
            //wordの場合を除く処理を作る
            //return false;
        }else{
            for(Hammer h: hammers){
                if(h != null){
                    if(h.getPosX() == posX && h.getPosY() == posY){
                        return false;
                    }else{
                        canPut_f = true;
                    }
                }else{
                    canPut_f = true;
                    break;
                }
            }  
            /*
            for(Word w: keyWord){
                if(w!=null){
                    if(w.getPosX() == posX && w.getPosY() == posY){
                        return false;
                    }else{
                        canPut_f = true;
                    }
                }else{
                    canPut_f = true;
                    break;
                }
            }  
            */

            return canPut_f;
        }
        return false;
    }

    //putItemメソッド
    //parameter 
    //  int itemType : アイテムの種類
    //  int itemNum  : アイテムの個数
    //return  
    //  --
    //
    public void putItem(int itemType, int itemNum){
        int x,y;
        int cnt = 0;

        if(itemType == TYPE_HAMMER){
            hammers = new Hammer[itemNum];
        }else if(itemType == TYPE_RED_STONE){

        }else{

        }

        while(cnt < itemNum){
            x = (int)(Math.random()*mapData.getWidth());
            y = (int)(Math.random()*mapData.getHeight());
            if(canPutItem(x,y)){
                switch(itemType){
                    case 0: //Hammer
                        hammers[cnt] = new Hammer(x,y);
                        System.out.println("Hammer created");
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
