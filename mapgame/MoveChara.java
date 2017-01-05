import javafx.scene.image.Image;

public class MoveChara implements Cloneable{
    public static final int TYPE_DOWN  = 0;
    public static final int TYPE_LEFT  = 1;
    public static final int TYPE_RIGHT = 2;
    public static final int TYPE_UP    = 3;

    public static final int PUNCH = 0;
    public static final int HAMMER = 1;

    private int[] charaDirPos;

    private int posX;
    private int posY;

    private MapData mapData;
    private ItemController itemController;

    private Image[] charaImage;
    private int count   = 0;
    private int diffx   = 1;
    private int charaDir;

    //コンストラクタ
    //parameter : 
    //  int,int - スタート地点
    //  MapData - 
    MoveChara(int startX, int startY, MapData mapData, ItemController itemController){
        this.mapData = mapData;
        this.itemController = itemController;
        charaImage = new Image[12];
        charaImage[0 * 3 + 0] = new Image("image/nekod1.png");
        charaImage[0 * 3 + 1] = new Image("image/nekod2.png");
        charaImage[0 * 3 + 2] = new Image("image/nekod3.png");
        charaImage[1 * 3 + 0] = new Image("image/nekol1.png");
        charaImage[1 * 3 + 1] = new Image("image/nekol2.png");
        charaImage[1 * 3 + 2] = new Image("image/nekol3.png");
        charaImage[2 * 3 + 0] = new Image("image/nekor1.png");
        charaImage[2 * 3 + 1] = new Image("image/nekor2.png");
        charaImage[2 * 3 + 2] = new Image("image/nekor3.png");
        charaImage[3 * 3 + 0] = new Image("image/nekou1.png");
        charaImage[3 * 3 + 1] = new Image("image/nekou2.png");
        charaImage[3 * 3 + 2] = new Image("image/nekou3.png");

        posX = startX;
        posY = startY;

        charaDir = TYPE_DOWN;
        charaDirPos = new int[2];
    }

	//クローン生成
    @Override
    public MoveChara clone() throws CloneNotSupportedException{
        MoveChara cloneChara = null;
        try{
            cloneChara = (MoveChara)super.clone();
        }catch(CloneNotSupportedException e){
            e.printStackTrace();
        }

        return cloneChara;
    }

	//クローン(this)が各オブジェクトのクローン(mapData,itemcont)を参照できるようにする
    public void setInitObj(MapData initMap,ItemController initIcon){
        this.mapData = initMap;
        this.itemController = initIcon;
    }

    //getIndexメソッド
    //parameter : --
    //return    : int
    //  向いてる方向と状態のイメージを選びたいため
    //  charDir*3+count
    //  というやりかたでインデックスを取得している
    //  この計算方法は直接インデックスを返すより都合が良い(charDir,countはその値で利用される)
    //
    public int getIndex(){
        return charaDir * 3 + count;
    }

    //changeCountメソッド
    //parameter : --
    //return    : --
    //
    //getIndexで使うcountを求める
    //つまり足としっぽの状態を計算する
    //
    public void changeCount(){
        count = count + diffx;
        if (count > 2) {
            count = 1;
            diffx = -1;
        } else if (count < 0){
            count = 1;
            diffx = 1;
        }
    }

    //getImageメソッド
    //  parameter : --
    //  return    : Image
    //  
    //カーソル押した後のイメージを取得する
    //
    public Image getImage(){
        changeCount();
        return charaImage[getIndex()];
    }

    public int getPosX(){
        return posX;
    }

    public int getPosY(){
        return posY;
    }

    public void setCharaDir(int cd){
        charaDir = cd;
    }

    // setCharaPosメソッド
    //  prameter --
    //  return   --
    // 猫の向いている方向の座標を取得する
    //
    public int[] getCharaDirPos(){

        if(charaDir == TYPE_DOWN){
            charaDirPos[0] = posX;
            charaDirPos[1] = posY+1;
        }else if(charaDir == TYPE_LEFT){
            charaDirPos[0] = posX-1;
            charaDirPos[1] = posY;
        }else if(charaDir == TYPE_RIGHT){
            charaDirPos[0] = posX+1;
            charaDirPos[1] = posY;
        }else{
            charaDirPos[0] = posX;
            charaDirPos[1] = posY-1;
        }
        return charaDirPos;
    }

    //canMoveメソッド
    //paramater : int dx,int dy
    //return    : boolean
    //
    //進むであろう座標になにも無ければtrueを返す
    //
    public boolean canMove(int dx, int dy){
        int frontObject = mapData.getMap(posX+dx,posY+dy);
        if (frontObject == MapData.TYPE_WALL){
            return false;
        } else if (frontObject == MapData.TYPE_NONE){
            return true;
        } else if (frontObject == MapData.TYPE_ITEM){
            return true;
        } else if (frontObject == MapData.TYPE_KEYWORD){
            return true;
        } else if (frontObject == MapData.TYPE_TRAP){
            return true;
        }else{
            return false;
        }
    }


    // moveメソッド
    //  parameter
    //    int dx,int dy : 進む距離
    // canMoveメソッドで移動する先にはなにもないと返されたときに実際に移動する
    //
    public void move(int dx, int dy){
        if (canMove(dx,dy)){
            posX += dx;
            posY += dy;
            if(itemController.asiatoTrapFlag){
                itemController.updateTrap(posX,posY,dx,dy);
            }
            if(mapData.getMap(posX,posY) == MapData.TYPE_KEYWORD){
                putUpKeyLetter();
            }else if(mapData.getMap(posX,posY) == MapData.TYPE_ITEM){
                putUpItem();
            }else{
            }
        }else{

        }
    }

    // putUpKeyLetterメソッド
    //   parameter
    //   --
    //   return
    //   --
    // 落ちているキーワードのかけらを拾う
    //
    public void putUpKeyLetter(){
        int stoneIndex = mapData.toStoneIndex(posX,posY);
        char letter = mapData.stone[stoneIndex].getKeyLetter();
        itemController.addPoach(letter);
    }

    // putUpItemメソッド
    //   parameter
    //   --
    //   return 
    //   --
    // 落ちているアイテムを拾う
    //
    public void putUpItem(){
        Item item = itemController.getItemData(posX,posY);
        itemController.addPoach(item.getItemType());
    }

    //useItemメソッド
    //  parameter
    //    Poach pickedItem : 取り出したアイテム
    //アイテムを使う
    public void useItem(Poach pickedItem){
        if(pickedItem.getItemType() == ItemController.ITEM_HAMMER){
            if(destroyStone(HAMMER)){
                System.out.println("detroy!");
            }else{
                System.out.println("hit!");
            }
        }else if(pickedItem.getItemType() == ItemController.ITEM_TIME){
            itemController.timerpuls = true;
            itemController.updatePoach(pickedItem);
        }else{
            if(mapData.canPutObject(posX, posY, MapData.TYPE_TRAP)){
                if(pickedItem.getItemType() == ItemController.ITEM_BANANA){
                    setTrap(Trap.TRAP_BANANA);
                    itemController.updatePoach(pickedItem);
                }else if(pickedItem.getItemType() == ItemController.ITEM_ASIATO){
                    itemController.asiatoTrapFlag = true;
                    setTrap(Trap.TRAP_ASIATO);
                    itemController.updatePoach(pickedItem);
                }else{
                    System.out.println("Please set Item on center of poachview.");
                }
            }else{
                System.out.println("can not put trap at here");
                mapData.printMap();
            }
        }
    }

    //destroyStoneメソッド
    //  parameter
    //    int attackType: 攻撃の手段
    //  return 
    //    boolean : 岩の耐久値を0にしたらtrueを返す
    //猫の攻撃
    //
    public boolean destroyStone(int attackType){
        int[] stonePos;
        int stoneIndex;
        int stonePosX,stonePosY;
        int damage = 0;

        if(attackType == PUNCH){
            damage = 1;
        }else if(attackType == HAMMER){
            damage = 10;
        }

        stonePos = getCharaDirPos();
        stonePosX = stonePos[0];
        stonePosY = stonePos[1];

        if(mapData.getMap(stonePosX,stonePosY) == MapData.TYPE_STONE){
            stoneIndex = mapData.toStoneIndex(stonePosX,stonePosY);
            mapData.stone[stoneIndex].decreaseHP(damage);
            if(mapData.stone[stoneIndex].getHP() <= 0){
                if(mapData.stone[stoneIndex].getKeyLetter() != '\u0000'){
                    mapData.setMap(stonePosX, stonePosY, MapData.TYPE_KEYWORD);
                }else{
                    mapData.setMap(stonePosX, stonePosY, MapData.TYPE_NONE);
                }
                return true;
            }else{
                return false;
            }
        }else{
            return false;
        }
    }

    // setTrapメソッド
    //   parameter
    //     int trapType : トラップの種類
    //   return 
    //     --
    // 現在地にトラップを仕掛ける
    //
    public void setTrap(int trapType){
        itemController.setTrapData(posX, posY, trapType);
        mapData.setMap(posX,posY,MapData.TYPE_TRAP);
    }
}

