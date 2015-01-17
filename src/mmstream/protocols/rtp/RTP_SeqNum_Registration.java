package mmstream.protocols.rtp;

import mmstream.util.*;
import mmstream.protocols.rtp.*;

public class RTP_SeqNum_Registration extends Object {

  // The register method of this class performs the check of the 
  // sequence number of incoming RTP packets.
  // It follows the algorithm described in RFC 1889, Appendix A.1

public RTP_SeqNum_Registration(RTP_RemoteSource src) {
  probation = RTProtocol.RTP_MIN_SEQ;
  baseSeqNum = 0;
  inSeqNum = 0;
  inExtSeqNum = 0;
  receivedCnt = 0;
  remotesrc = src;
}

private void init(long seq) {
  baseSeqNum = seq - 1;
  inSeqNum = seq;
  badSeqNum=RTProtocol.RTP_SEQ_MOD + 1;
  inExtSeqNum = 0;
  receivedCnt =0;
}

protected  boolean register(long new_num) {

  if (probation > 0) {
    // in sequence, probation time reduced
    if (new_num == inSeqNum + 1) {
      probation--;
      inSeqNum = new_num;
      // probation finished
      if (probation == 0) {
	init(new_num);
	receivedCnt++;
	remotesrc.publish();
	return true;
      }
    }
    // not in sequence, probation starts again
    else {
      probation = RTProtocol.RTP_MIN_SEQ - 1;
      inSeqNum = new_num;
    }
    return false;
  }

  // no probation
  else {

    long delta = new_num - inSeqNum;
    // wrap around
    if (delta < 0)
      delta += RTProtocol.RTP_SEQ_MOD;
    
    // everything alright
    if (delta < RTProtocol.RTP_MAX_DROPOUT) {
      // wrap  around
      if (new_num < inSeqNum) 
	inExtSeqNum += RTProtocol.RTP_SEQ_MOD;
      inSeqNum = new_num;
    }

    else if (delta <= RTProtocol.RTP_SEQ_MOD - RTProtocol.RTP_MAX_MISORDER) {
      if (new_num == badSeqNum) {
	remotesrc.setValid(true);
	init(new_num);
	// TODO reset received counter
      }
      else {
	badSeqNum = (new_num + 1) & RTProtocol.RTP_SEQ_MOD;
	remotesrc.setValid(false);
	return false;
      }
    }
    else {
      // TODO evtl. doch noch einfuegen?
    }
  }  

  receivedCnt++;

  return true;
} // boolean register(int new_num)

protected  long 
highest() { return inSeqNum; }
 
protected  long
highestExt() { return inExtSeqNum + inSeqNum; }

protected  long 
expected() { return (inExtSeqNum + inSeqNum - baseSeqNum + 1); }

protected  long 
fractionLost() {
  long exp = this.expected();
  long exp_int = exp - expectedPrior;
  expectedPrior = exp;
  long rec_int = receivedCnt - receivedPrior;
  receivedPrior = receivedCnt;
  long lost_int = exp_int - rec_int;
  if (exp_int == 0 || lost_int <= 0)
    return 0;
  else
    return  (long)(((lost_int << 8) / exp_int) & 0xff);
}

protected long 
totalLost() { return inExtSeqNum + inSeqNum - baseSeqNum - receivedCnt; }

private long receivedCnt;
private long probation;
private long inSeqNum;
private long inExtSeqNum;
private long baseSeqNum;
private long badSeqNum;
private long expectedPrior;
private long receivedPrior;
private RTP_RemoteSource remotesrc;
}

