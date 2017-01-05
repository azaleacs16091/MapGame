import javafx.scene.image.Image;
import java.util.Vector;
import javax.swing.SwingWorker;
import java.util.Random;
import java.util.Arrays;

public class MoveCaw implements UpdateEvent, Cloneable 
{
  protected Vector< Listener > listener = new Vector<Listener>();

  public final int vy[] = {1, 0, 0, -1};
  public final int vx[] = {0, -1, 1, 0};
  
  public static final int TYPE_DOWN  = 0;
  public static final int TYPE_LEFT  = 1;
  public static final int TYPE_RIGHT = 2;
  public static final int TYPE_UP    = 3;

  private int posX;
  private int posY;

  private MapData mapData;
  private ItemController itemController;
  private MoveChara charaData;

  private Image[] charaImage;
  private int count   = 0;
  private int diffx   = 1;
  private int charaDir;

  private boolean execute; // AI を動かすかどうか

  private Random rand;

  private boolean isstoped; // 罠に引っかかっているか

  private int[] log; // AIが歩いた履歴(なるべく訪れていないところを訪れるようにしたいため)
  

  //コンストラクタ
  //parameter : 
  //  MapData - 
  MoveCaw(MapData mapData, ItemController itemController, MoveChara charaData)
  {
    this.rand = new Random();
    this.mapData = mapData;
    this.itemController = itemController;
    this.charaData = charaData;

    this.log = new int[mapData.getWidth() * mapData.getHeight()];
    Arrays.fill(log, -13333);

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

    setCaw();
    
    execute = false;
    isstoped = false;
    
  }

  //クローン生成
  @Override
  public MoveCaw clone() throws CloneNotSupportedException{
      MoveCaw cloneCaw = null;
      try{
          cloneCaw = (MoveCaw)super.clone();
      }catch(CloneNotSupportedException e){
          e.printStackTrace();
      }
      return cloneCaw;
  }

  //クローン(this)が各オブジェクトのクローン(mapData,itemcont,chara)を参照できるようにする
  public void setInitObj(MapData initMap, ItemController initIcon, MoveChara initChara){
      this.mapData = initMap;
      this.itemController = initIcon;
      this.charaData = initChara;
  }

  public boolean isstoped()
  {
    return(isstoped);
  }
  
  // 牛を適当な開いている位置にセットする
  private void setCaw()
  {
    Vector< Integer > StartIdx = new Vector< Integer >();

    // 同じところにいると詰む可能性があるのでなるべく離す(厳密ではない
    int size = mapData.getHeight() * mapData.getWidth();
    UnionFind tree = new UnionFind(size + 1);
    for(int i = 0; i < mapData.getHeight(); i++) {
      for(int j = 0; j < mapData.getWidth(); j++) {
        if(mapData.getMap(j, i) == mapData.TYPE_NONE || mapData.getMap(j, i) == mapData.TYPE_ITEM) {
          for(int k = 0; k < 4; k++) {
            int nx = j + vx[k], ny = i + vy[k];
            if(mapData.getMap(nx, ny) == mapData.TYPE_NONE || mapData.getMap(nx, ny) == mapData.TYPE_ITEM) {
              tree.unite(mapData.toIndex(j, i), mapData.toIndex(nx, ny));
            }
          }
        }
      }
    }
    tree.unite(mapData.toIndex(charaData.getPosX(), charaData.getPosY()), size);
    for(int i = 0; i < mapData.getHeight(); i++) {
      for(int j = 0; j < mapData.getWidth(); j++) {
        if(mapData.canPutObject(j, i, mapData.TYPE_ENEMY) && tree.find(mapData.toIndex(j, i)) != tree.find(size)) {
          StartIdx.add(mapData.toIndex(j, i));
        }
      }
    }
    
    int idx = rand.nextInt(StartIdx.size());
    posX = StartIdx.elementAt(idx) % mapData.getWidth();
    posY = StartIdx.elementAt(idx) / mapData.getWidth();
    log[mapData.toIndex(getPosX(), getPosY())] = 0;
    charaDir = TYPE_DOWN;
  }
  

  public void start()
  {
    System.out.println("foo");
    execute = true;
    new AIWorker(300).execute();
  }

  public void stop()
  {
    posX = -1;
    posY = -1;
    execute = false;
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

  public int getPosX(){
    return posX;
  }

  public int getPosY(){
    return posY;
  }

  public void setCharaDir(int cd)
  {
    charaDir = cd;
  }

  //canMoveメソッド
  //paramater : int dx,int dy
  //return    : boolean
  //
  //進むであろう座標になにも無ければtrueを返す
  //
  public boolean canMove(int dx, int dy)
  {
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


  //moveメソッド
  //parameter : int dx,int dy
  //return    : boolean
  //
  //canMoveメソッドで移動する先にはなにもないと返されたときに
  //実際に移動する
  //
  public boolean move(int dx, int dy)
  {
      if (canMove(dx,dy)){
          posX += dx;
          posY += dy;
          return true;
      }else{
          return false;
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

  class AIWorker extends SwingWorker<Object,Object> implements Cloneable
  {
    private int sleepTime;

    AIWorker(int sleep)
    {
      this.sleepTime = sleep;
    }

    @Override
    protected Object doInBackground()
    {
      try {
        // 罠があったら 呼び出し間隔を 6 倍
        if(itemController.isTrapPoint(getPosX(), getPosY())) {
          isstoped = true;
          Thread.sleep(this.sleepTime * 6);
          itemController.removeTrapData(getPosX(),getPosY());
          isstoped = false;
        } else {
          Thread.sleep(this.sleepTime);
        }
      } catch(InterruptedException e) {
        e.printStackTrace();
      }
      return(null);
    }
    
    @Override
    protected void done()
    {
      // ここでAIの動きを決定する
      Vector< Integer > StartIdxHeavy = new Vector< Integer >();
      if(mapData.toIndex(getPosX(), getPosY()) < 0) return;
      int now = log[mapData.toIndex(getPosX(), getPosY())];
      // 過去 5 回(くらい) に訪れたマスは再び訪れない
      int visible = now - 5;
      int smaller = 214325436;
      for(int i = 0; i < 4; i++) {
        if(canMove(vx[i], vy[i])) {
          smaller = Math.min(smaller, log[mapData.toIndex(getPosX() + vx[i], getPosY() + vy[i])]);
          if(log[mapData.toIndex(getPosX() + vx[i], getPosY() + vy[i])] <= visible) {
            StartIdxHeavy.add(i);         
          }
        }
      }

      
      
      if(StartIdxHeavy.isEmpty()) {
        for(int i = 0; i < 4; i++) {
          if(canMove(vx[i], vy[i]) && log[mapData.toIndex(getPosX() + vx[i], getPosY() + vy[i])] == smaller) {
            StartIdxHeavy.add(i);
          }
        }
      }
      
      if(StartIdxHeavy.isEmpty()) return;
      
      int rnd = StartIdxHeavy.elementAt(rand.nextInt(StartIdxHeavy.size()));
      move(vx[rnd], vy[rnd]);
      setCharaDir(rnd);
      log[mapData.toIndex(getPosX(), getPosY())] = now + 1;

      //System.out.println(getPosX() + " " + getPosY());
      
      setValue(this);
      if(execute) new AIWorker(this.sleepTime).execute();
    }
    
  }
  


  // 変わった値をセットして相手に伝える
  @Override
  public void setValue(Object obj)
  {
    this.findValueChanged(obj);
  }

  // 特に意味はないけど
  @Override
  public Object getValue()
  {
    return(this);
  }

  // 伝える相手を追加する
  @Override
  public void addListener(UpdateEvent.Listener l)
  {
    this.listener.add(l);
  }

  // 相手を消す
  @Override
  public void removeListener(UpdateEvent.Listener l)
  {
    this.listener.removeElement(l);
  }

  // 相手に伝える
  protected void findValueChanged(Object obj)
  {
    int size = this.listener.size();
    UpdateEvent.Event e = new UpdateEvent.Event(this, obj);
    for(int i = 0; i < size; i++) {
      ((UpdateEvent.Listener)this.listener.elementAt(i)).valueChanged(e);
    }
  }

  // 連結状況を管理する
  public class UnionFind
  {
    private int[] data;

    UnionFind(int sz)
    {
      data = new int[sz];
      Arrays.fill(data, -1);
    }
    int find(int y)
    {
      if(data[y] < 0) return(y);
      return(data[y] = find(data[y]));
    }
    int unite(int x, int y)
    {
      x = find(x);
      y = find(y);
      if(x != y) {
        data[x] += data[y];
        data[y] = x;
      }
      return(find(x));
    }

  }
}

