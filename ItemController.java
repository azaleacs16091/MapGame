import javafx.scene.image.Image;
import java.util.ArrayList;
import java.util.Arrays;
public class ItemController implements Cloneable{
    public static final int ITEM_NONE = 0;
    public static final int ITEM_HAMMER = 1;
    public static final int ITEM_BANANA = 2;
    public static final int ITEM_ASIATO = 3;
    public static final int ITEM_TIME = 4;
    private MapData mapData;
    private Item[] mappedItem;
    private ArrayList<Trap> mappedTrap;
    private ArrayList<Poach> keyLetterPoach;
    private Poach[] itemPoach;
    private Image[] itemImage;
    private Image[] trapImage;
    private int[][] itemPoint;
    private int item_cnt = 0;
    private int mappedItemNum = 0;//実際にセットしたアイテム数(initMapItemのmapItemNumはアイテムポイントの数)
    private int maxNum = 9; // アイテムの最大所持数+4
    private int possessionNum = 0;//現在の所持数 
    private int itemTypeNum = 3;//ITEM_NONE以外のアイテムの数
    //↓ にパスワードにするキーワードを入れていく
    private static String[] keyWordList = {"test","apple","phone","dog"};
    public boolean asiatoTrapFlag = false;//アイテム"足跡5歩分消す奴"使用フラグ(このトラップの効果は未実装)
    public boolean timerpuls = false;//アイテム"砂時計のアイコンのやつ"使用フラグ

    ItemController(MapData mapData){
        this.mapData = mapData;
        itemPoint = new int[mapData.getMapItemNum()][2];
        mappedItem = new Item[mapData.getMapItemNum()];
        setItemPoint(); //アイテムポイントの座標を格納してるだけ
        initMapItem(mapData.getMapItemNum());//アイテムをマップ上に設置する
        keyLetterPoach = new ArrayList<Poach>();
        mappedTrap = new ArrayList<Trap>();
        initImage(); //イメージ関連の初期化
        initPoach(); //ポーチの初期化
    }

	//クローン生成
    @Override
    public ItemController clone() throws CloneNotSupportedException{
        ItemController cloneIcon = null;
        Item[] cloneItem = new Item[mapData.getMapItemNum()];
        try{
            cloneIcon = (ItemController)super.clone();
			//ディープコピー
            for(int i = 0;i < mapData.getMapItemNum();i++){
                cloneItem[i] = (Item)mappedItem[i].clone(); 
            }
            cloneIcon.mappedItem = cloneItem;
        }catch(CloneNotSupportedException e){
            e.printStackTrace();
        }
        return cloneIcon;
    }

	//クローン(this)が各オブジェクトのクローン(mapData)を参照できるようにする
    public void setInitObj(MapData initMap){
        this.mapData = initMap;
    }

    // initMapメソッド
    //   parameter 
    //     int mapItemNum : アイテムポイントの数
    //   return --
    // マップに配置するアイテムデータの初期化
    //
    private void initMapItem(int mapItemNum){
        int randItemType;//HAMMER,BANANA,ASIATOのどれか
        //各アイテムは最低１つは設置する
        int hammerNum = 1;
        int bananaNum = 1;
        //int asiatoNum = 1;
        int timeNum   = 1;

        int restNum = mapItemNum-(hammerNum+bananaNum+timeNum);

        //残りのアイテムポイントに設置するアイテムをランダムで決める
        while(0 < restNum){
            randItemType = (int)(Math.random()*(itemTypeNum-1))+1;
            if(randItemType == ITEM_HAMMER){
                if(hammerNum < 2){
                    hammerNum++;
                    restNum--;
                }
            }else if(randItemType == ITEM_BANANA){
                bananaNum++;
                restNum--;
            }else{
                timeNum++;
                restNum--;
            }
        }

        putItem(ITEM_HAMMER, hammerNum);
        putItem(ITEM_BANANA, bananaNum);
        //putItem(ITEM_ASIATO, asiatoNum);
        putItem(ITEM_TIME, timeNum);
    }

    private void initImage(){
        itemImage = new Image[5];
        trapImage = new Image[2];
        itemImage[ITEM_NONE]   = new Image("image/ITEM_NONE.jpg");
        itemImage[ITEM_HAMMER] = new Image("image/HAMMER.png");
        itemImage[ITEM_BANANA] = new Image("image/BANANA.png");
        itemImage[ITEM_ASIATO] = new Image("image/ITEM_ASIATO.png");//仮
        itemImage[ITEM_TIME] = new Image("image/TIME.png");//仮
        trapImage[0] = new Image("image/BANANA_TRAP.png");
        trapImage[1] = new Image("image/ASIATO_TRAP.png");//仮

    }

    // このitemPoachの4つの初期化はポーチの表示のために行っている
    public void initPoach(){
        itemPoach = new Poach[maxNum];
        Arrays.fill(itemPoach,null);
        itemPoach[0] = new Poach(ITEM_NONE,0);
        itemPoach[1] = new Poach(ITEM_NONE,0);
        itemPoach[2] = new Poach(ITEM_NONE,0);
        itemPoach[3] = new Poach(ITEM_NONE,0);
        keyLetterPoach.clear();
        mappedTrap.clear();
    }

    // getKeyLetterPoachメソッド
    //   parameter --
    //   return 
    //     String : 現在ポーチに入ってる文字(列)
    // 拾った文字(列)を取得する
    //
    public String getKeyLetterPoach(){
        String str = "";
        for(Poach klp: keyLetterPoach){
            str += klp.getLetter();
        }
        return str;
    }

    // decidekeyWordメソッド
    //   return 
    //     String : 今回のパスワードとなるキーワード
    // パスワードとなるキーワードをキーワードリストの中からランダムで決める
    //
    public static String decideKeyWord(){
        int x = (int)(Math.random()*keyWordList.length);
        return keyWordList[x];
    }

    // putItemメソッド
    //  parameter 
    //    int itemType : アイテムの種類
    //    int itemNum  : アイテムの個数
    //  return 
    //    --
    // アイテムをマップ上に設置(画面上に表示はしてない)
    //
    private void putItem(int itemType, int itemNum){
        int[] point = new int[2];
        int x,y;
        int cnt = 0;
        while(cnt < itemNum){
            point = getItemPoint();
            x = point[0];
            y = point[1]; 
            if(noItemAt(x,y)){
                mappedItem[mappedItemNum] = new Item(x,y,itemType);
                mappedItemNum++;
                cnt++;
            }
        }
    }

    // noItemAtメソッド
    //  parameter 
    //    アイテム座標
    //  return
    //    その座標に他のアイテムが置かれてなければtrueを返す
    // 指定した座標に他のアイテムがすでに置かれていないかどうかを確かめる
    //
    private boolean noItemAt(int x, int y){
        for(Item i: mappedItem){
            if(i != null){
                if(i.getPosX() == x  && i.getPosY() == y){
                    return false;
                }
            }else{
                return true;
            }
        } 
        return true;
    }

    // getItemPointメソッド
    //  parameter --
    //  return    
    //    int[] : アイテムポイントのx,y座標
    // ランダムでアイテムポイントを選ぶ
    //
    private int[] getItemPoint(){
       int randNum = (int)(Math.random()*mapData.getMapItemNum()); 
       return itemPoint[randNum]; 
    }

    // isItemPointメソッド
    //   parameter 
    //     int x,y : 座標
    //   return 
    //     boolean : 座標がアイテムポイントのときtrueを返す
    // 指定した座標がアイテムポイントかどうか確かめる 
    //
    private boolean isItemPoint(int x, int y){
        if(mapData.getMap(x,y) == MapData.TYPE_ITEM){
            return true;
        }else{
            return false;
        }
    }

    // setItemPointメソッド
    //  parameter --
    //  return    --
    // アイテムポイントの座標を保存する
    //
    private void setItemPoint(){
        for(int y = 0;y < mapData.getHeight();y++){
            for(int x = 0;x < mapData.getWidth();x++){
                if(isItemPoint(x,y)){
                    itemPoint[item_cnt][0] = x;
                    itemPoint[item_cnt][1] = y;
                    item_cnt++;
                }
                if(item_cnt >= mapData.getMapItemNum()){
                    break;
                }
            }
        }
    }

    // getItemメソッド
    //  parameter
    //    int x,y : アイテムの座標
    //  return 
    //    Item    : その座標にあるアイテム情報 
    // (x,y)地点のアイテム情報を取得する
    //
    public Item getItemData(int x, int y){
        int itemIndex = 0;
        for(int i = 0;i < mappedItem.length;i++){
            if(mappedItem[i].getPosX() == x  &&  mappedItem[i].getPosY() == y){
                itemIndex = i;
            }
        }
        return mappedItem[itemIndex];
    }

    // getItemImageメソッド
    //  parameter
    //    int x,y : アイテムの座標
    //  return 
    //    Image   : そのアイテムの画像
    // (x,y)地点にあるアイテムの画像を取得
    //
    public Image getItemImage(int x, int y){
        int index = 0;
        for(Item i: mappedItem){
            if(i.getPosX() == x && i.getPosY() == y){
                switch(i.getItemType()){
                    case ITEM_NONE:
                        index = ITEM_NONE;
                        break;
                    case ITEM_HAMMER:
                        index = ITEM_HAMMER;
                        break;
                    case ITEM_BANANA:
                        index = ITEM_BANANA;
                        break;
                    case ITEM_ASIATO:
                        index = ITEM_ASIATO;
                        break;
                    case ITEM_TIME:
                        index = ITEM_TIME;
                        break;
                    default:
                        break;
                }
                break;
            }
        }
        return itemImage[index];
    }

    // getItemImageメソッド(オーバーロード)
    //  parameter
    //    int x : アイテムの種類
    //  return 
    //    Image   : そのアイテムの画像
    // アイテムの画像を取得
    //
    public Image getItemImage(int itemType){
        return itemImage[itemType];
    }

    // addPoachメソッド
    //  parameter
    //    char keyLetter : 拾った1文字
    //  return 
    //    --
    // 拾ったキーワードをポーチに入れる
    //
    public void addPoach(char keyLetter){
        Poach pickedKeyLetter = new Poach(keyLetter);
        keyLetterPoach.add(pickedKeyLetter);
    }

    // addPoachメソッド(オーバーロード)
    //  parameter
    //    int itemType : ポーチに入れたいアイテムの種類
    //  return -- 
    // アイテムをポーチに入れる
    //
    public void addPoach(int itemType){
        int itemNum = 1;
        if(itemType == ITEM_BANANA){
            itemNum = 5;
        }

        //itemPoach[]の最初の2要素と最後の2要素はITEM_NONEをセットしその間にゲットしたアイテムを追加していく
        //← ポーチの状態を画面上にうまく表示するため
        Poach temp = itemPoach[possessionNum+2];
        itemPoach[possessionNum+2] = new Poach(itemType,itemNum);
        itemPoach[possessionNum+4] = temp;

        possessionNum++;
    } 

    // removePoachメソッド
    //   parameter
    //     Poach lostItem : 残り使用回数が0になったアイテム
    //   return --
    // そのアイテムをポーチから削除する
    //
    public void removePoach(Poach lostItem){
        int lostIndex = 0;

        //使いきったアイテムのitemPoach[]上でのインデックスを取得
        for(int i = 0;i < possessionNum+4;i++){
            if(itemPoach[i].equals(lostItem)){
                lostIndex = i;
            }
        }

        //そのインデックス以降に格納されているアイテムを1つずつずらす
        for(int j = lostIndex;j < possessionNum+3;j++){
            itemPoach[j] = itemPoach[j+1];
        }

        possessionNum--;
    }

    // updatePoachメソッド
    //   parameter 
    //     Poach usedItem : 使用されたアイテム
    //   return --
    // ポーチを更新する(アイテムの残り所持数等)
    //
    public void updatePoach(Poach usedItem){
        usedItem.setRestNum(-1);
        if(usedItem.getRestNum() <= 0){
            removePoach(usedItem);
        }
    }

    //getPoachDataメソッド
    //  parameter
    //    int poachIndex : itemPoach[]のインデックス
    //  return 
    //    Poach      : ポーチのインデックス番目に入っているアイテム情報
    //ポーチに入っているアイテムデータを取得する
    public Poach getPoachData(int poachIndex){
        return itemPoach[poachIndex];
    }

    // getPossessionNumメソッド
    //   parameter --
    //   return 
    //     int : アイテムの所持数を返す
    //
    public int getPossessionNum(){
        return possessionNum;
    }

    // poachHasItemメソッド
    //  parameter  --
    //  return 
    //    boolean : itemPoachにアイテムが存在すればtrueを返す
    // アイテムを所持しているかを確認する
    //
    public boolean poachHasItem(){
        if(itemPoach[2].getItemType()!=ITEM_NONE){
            return true;
        }else{
            return false;
        }
    }

    // getTrapメソッド
    //  parameter
    //    int x,y : トラップのある座標
    //  return 
    //    Trap    : その座標にあるトラップ情報 
    // (x,y)地点のトラップ情報を取得する
    //
    public Trap getTrapData(int x, int y){
        int cnt = 0;
        int trapIndex = 0;
        for(Trap t: mappedTrap){
            int[][] trapPos = t.getTrapPos(); 
            for(int i = 0;i < t.getStepNum();i++){
                if(trapPos[i][0] == x  &&  trapPos[i][1] == y){
                    trapIndex = cnt;
                    break;
                }
            }
            cnt++;
        }
        return mappedTrap.get(trapIndex);
    }

    //setTrapDataメソッド
    //  parameter
    //    int x,y      : 座標(キャラクターの現在位置)
    //    int trapType : トラップの種類
    //  return --
    //トラップ情報の追加
    //
    public void setTrapData(int x, int y, int trapType){
        Trap newTrap = new Trap(x,y,trapType);
        mappedTrap.add(newTrap);
    }

    // updateTrapメソッド
    //   parameter
    //     int x,y : 現在の座標
    //     int dx,dy : 前の座標との差分
    //   return --
    // 足跡を消すアイテム(有効歩数:5)使用時の処理. トラップの座標を追加している
    //
    public void updateTrap(int x, int y, int dx, int dy){
        Trap thisTrap = getTrapData(x-dx,y-dy);
        thisTrap.setTrapPos(x,y);
        mapData.setMap(x,y,MapData.TYPE_TRAP);
        if(thisTrap.getStepNum() > 4){
            asiatoTrapFlag = false;
        }
    }

    // removeTrapDataメソッド
    //   parameter 
    //     int x,y : トラップ座標
    // 発動し終えたトラップをマップから除去
    public void removeTrapData(int x, int y){
        int[] trapPos;
        for(Trap usedTrap: mappedTrap){
            trapPos = usedTrap.getTrapPos(0);
            if(x == trapPos[0] && y == trapPos[1]){
                mappedTrap.remove(usedTrap);
                mapData.setMap(x,y,MapData.TYPE_NONE);
            }
        }
    }

    // isTrapPointメソッド
    //  parameter
    //    int x,y : マップ座標
    //  return 
    //    boolean : 指定された座標にトラップがあればtrueを返す
    // (x,y)にトラップが仕掛けられているかどうかを確認する
    //
    public boolean isTrapPoint(int x, int y){
        if(mapData.getMap(x,y) == MapData.TYPE_TRAP){
            return true;
        }else{
            return false;
        }
    }

    // getTrapImageメソッド
    //  parameter
    //    int x,y : トラップの座標
    //  return 
    //    Image   : そのトラップを表す画像
    // (x,y)地点にあるトラップを表す画像を取得
    //
    public Image getTrapImage(int x, int y){
        int index = 0;
        for(Trap t: mappedTrap){
            int[][] trapPos = t.getTrapPos(); 
            for(int i = 0;i < t.getStepNum();i++){
                if(trapPos[i][0]== x && trapPos[i][1] == y){
                    switch(t.getTrapType()){
                        case Trap.TRAP_BANANA:
                            index = 0;
                            break;
                        case Trap.TRAP_ASIATO:
                            index = 1;
                            break;
                        default:
                            break;
                    }
                    break;
                }
            }
        }
        return trapImage[index];
    }

    // getTrapImageメソッド(オーバーロード)
    //  parameter
    //    int x : トラップの種類
    //  return 
    //    Image   : そのトラップの画像
    // トラップの画像を取得
    //
    public Image getTrapImage(int trapType){
        int index;
        if(trapType == Trap.TRAP_BANANA){
            index = 0;
        }else{
            index = 1;
        }
        return trapImage[index];
    }
}

