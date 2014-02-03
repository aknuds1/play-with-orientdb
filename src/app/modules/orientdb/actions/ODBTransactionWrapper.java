package modules.orientdb.actions;

import modules.orientdb.Model;
import modules.orientdb.ODB;
import modules.orientdb.ODB.DBTYPE;
import modules.orientdb.annotation.Transactional;
import play.mvc.Action;
import play.mvc.Http;
import play.mvc.SimpleResult;
import play.libs.F.Promise;

public class ODBTransactionWrapper extends Action<Transactional>
{
    @Override
    public Promise<SimpleResult> call(Http.Context context) throws Throwable
    {
    	Promise<SimpleResult> res;
    	//internalServerError();
    	try {
	    	beforeInvocation();
	        res = delegate.call(context);
	        onInvocationSuccess();
    	} catch (Exception e) {
    		System.out.println("exception happened.");
    		e.printStackTrace();
    		onInvocationException(e);
            throw e;
    	} finally {
    		System.out.println("cleanup");
    		invocationFinally();
    	}
    	return res;
    }
    
    public void beforeInvocation() {
    	DBTYPE type = this.getClass().getAnnotation(Transactional.class).db();
    	if(type == DBTYPE.DOCUMENT || type == DBTYPE.OBJECT)
    		Model.objectDB().begin();
    }
    
    public void invocationFinally() {
        ODB.close();
    }
    
    public void onInvocationException(Throwable e) {
        ODB.rollback();
    }

    public void onInvocationSuccess() {
        ODB.commit();
    }

}
