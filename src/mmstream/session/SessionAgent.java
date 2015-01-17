package mmstream.session;

import mmstream.address.*;
import mmstream.config.*;
import mmstream.source.*;
import mmstream.util.*;
import mmstream.connection.*;
import mmstream.protocols.*;
import mmstream.config.*;
import mmstream.session.*;
import mmstream.stream.*;

public interface SessionAgent {

  public abstract void finish();

  public abstract void exportSession(Session s, int action);

  public abstract void addSessionImport(Connection con, SessionMapper sm) throws Session_Exception;
  public abstract void deleteSessionImport(Connection con);

  public abstract void addSessionExport(Connection con, SessionMapper sm, long repeatIntervall) throws Session_Exception;
  public abstract void deleteSessionExport(Connection con);

  public abstract Connection getExportConnection(Address ad);
  public abstract Connection getImportConnection(Address ad);

  public abstract SessionMapper getSessionMapper(Address ad);

}
