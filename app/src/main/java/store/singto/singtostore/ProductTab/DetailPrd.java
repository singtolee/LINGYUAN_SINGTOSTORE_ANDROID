package store.singto.singtostore.ProductTab;

import java.util.List;
import java.util.Map;

/**
 * Created by Singto on 1/10/2017 AD.
 */

public class DetailPrd {
    String prdName;
    String prdSub;
    String prdPrice;
    String prdPackageInfo;
    Boolean prdRefundable;
    String prdSuppler;
    List<String> prdImages;
    List<String> prdInfoImages;
    List<String> prdCS;
    List<String> prdCSQty;
    public void reset(){
        if(this.prdInfoImages!=null){
            this.prdInfoImages.clear();
        }
        if(this.prdImages!=null){
            this.prdImages.clear();
        }
        if(this.prdCS!=null){
            this.prdCS.clear();
        }
        if(this.prdCSQty!=null){
            this.prdCSQty.clear();
        }
    }
}
