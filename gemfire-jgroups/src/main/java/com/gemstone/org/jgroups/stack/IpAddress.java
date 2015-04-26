/** Notice of modification as required by the LGPL
 *  This file was modified by Gemstone Systems Inc. on
 *  $Date$
 **/
// $Id: IpAddress.java,v 1.29 2005/11/07 09:44:17 belaban Exp $

package com.gemstone.org.jgroups.stack;

import com.gemstone.org.jgroups.Address;
import com.gemstone.org.jgroups.Global;
import com.gemstone.org.jgroups.JChannel;
import com.gemstone.org.jgroups.JGroupsVersion;
import com.gemstone.org.jgroups.util.GemFireTracer;
import com.gemstone.org.jgroups.util.StreamableFixedID;

import java.io.*;
import java.net.InetAddress;
import java.net.InetSocketAddress;
import java.net.SocketAddress;



/**
 * Network-dependent address (Internet). Generated by the bottommost layer of the protocol
 * stack (UDP). Contains an InetAddress and port.
 * @author Bela Ban
 */
public class IpAddress implements StreamableFixedID,
                                  Address {
  private static final long serialVersionUID = -294637383250428305L;
  
    private InetAddress             ip_addr=null;
    private int                     port=0;
    private byte[]                  additional_data=null;
    protected static final GemFireTracer     log=GemFireTracer.getLog(IpAddress.class);

    private static final String MEMBER_WEIGHT_PREFIX = "zzzmbrwgt"; // GemStoneAddition
    
    public static boolean                  resolve_dns=true; // GemStoneAddition - resolve names by default
    private transient int                   size=-1;
    /** GemStoneAddition - can this member become the group coordinator? */
    private boolean shouldNotBeCoordinator;
    /** GemStoneAddition - does this member have split-brain detection enabled? */
    private boolean splitBrainEnabled;
    private byte memberWeight;
    /** GemStoneAddition - member GemFire version */
    private transient short version = JGroupsVersion.CURRENT_ORDINAL;

//    static {
//        /* Trying to get value of resolve_dns. PropertyPermission not granted if
//        * running in an untrusted environment  with JNLP */
//        try {
//            resolve_dns=Boolean.valueOf(System.getProperty("resolve.dns", "false")).booleanValue();
//        }
//        catch (SecurityException ex){
//            resolve_dns=false;
//        }
//    }

  // GemStoneAddition - defaults to true
  public boolean preferredForCoordinator() {
    return !shouldNotBeCoordinator;
  }
  
  // GemStoneAddition - defaults to false
  public boolean splitBrainEnabled() {
    return this.splitBrainEnabled;
  }
  
  // GemStoneAddition - member weight
  public void setMemberWeight(int weight) {
    this.memberWeight = (byte)Math.min(weight, 255);
  }
  
  // GemStoneAddition - member weight
  public int getMemberWeight() {
    return this.memberWeight;
  }

  /** GemStoneAddition - pids help with debugging quite a bit */
  public void setProcessId(int pid) {
    this.processId = pid;
  }
  
  /** GemStoneAddition - get the pid if any */
  public int getProcessId() {
    return this.processId;
  }
  
  /** GemStoneAddition - can this member be the GMS coordinator? */
  public void shouldntBeCoordinator(boolean shouldNotBe) {
    this.shouldNotBeCoordinator = shouldNotBe;
  }
  
  /** GemStoneAddition - sets whether this member has split-brain detection enabled */
  public void splitBrainEnabled(boolean enabled) {
    this.splitBrainEnabled = enabled;
  }
  
  /**
   * GemstoneAddition returns a number that can be used to differentiate two
   * addresses with the same InetAddress and port.  This ID is not used in
   * equality comparisons so that the coordinator can distinguish between
   * old and new IDs.  Instead, equality comparisons use the birthViewId so
   * that post-join comparisons of IpAddresses can easily distinguish between
   * new and old, reused addresses.
   */
  public int getUniqueID() {
    return this.directPort != 0? this.directPort : this.processId;
  }
  
  /**
   * get the roles of this GemFire member (GemStoneAddition)
   */
  public String getName() {
    return this.name == null? "" : this.name;
  }
  
  /**
   * get the roles of this GemFire member (GemStoneAddition)
   */
  public String[] getRoles() {
    if (this.additional_data != null) {
      try {
        DataInput di = new DataInputStream(new ByteArrayInputStream(this.additional_data));
        return JChannel.getGfFunctions().readStringArray(di);
      } catch (Exception e) {
        throw new RuntimeException("unable to read roles", e);
      }
    }
    return new String[0];
  }
  
  /**
   * set the durable client attributes (GemStoneAddition)
   */
  public void setDurableClientAttributes(Object d) {
    durableClientAttributes = d;
  }
  
  /**
   * get the durable client attributes (GemStoneAddition)
   */
  public Object getDurableClientAttributes() {
    return durableClientAttributes;
  }
  
  /**
   * set the roles of this GemFire member (GemStoneAddition)
   */
  public void setRoles(String[] roles) {
    // use additional_data to hold roles
    if (roles.length > 0) {
      try {
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput dao = new DataOutputStream(baos);
        JChannel.getGfFunctions().writeStringArray(roles, dao);
        this.additional_data = baos.toByteArray();
      } catch (Exception e) {
        throw new RuntimeException("unable to serialize roles", e);
      }
    }
  }
  
  /** GemStoneAddition - process id */
  private int processId;
  private int vmKind;
  private int birthViewId = -1;
  private int directPort;
  private String name;
  private Object durableClientAttributes;

  
    // Used only by Externalization
    public IpAddress() {
    }
    
    public IpAddress(String i, int p) {
        port=p;
    	try {
        	ip_addr=InetAddress.getByName(i);
        }
        catch(Exception e) {
            if(log.isWarnEnabled()) log.warn("failed to get " + i + ": " + e);
        }
        if(this.ip_addr == null)
            setAddressToLocalHost();
//        setGemFireAttributes(MemberAttributes.DEFAULT); // GemStoneAddition
    }



    public IpAddress(InetAddress i, int p) {
        ip_addr=i; port=p;
        if(this.ip_addr == null)
            setAddressToLocalHost();
        JChannel.getGfFunctions().setDefaultGemFireAttributes(this); // GemStoneAddition
    }


    private void setAddressToLocalHost() {
        try {
          // GemStoneAddition - use the configured GemFire bind address, if present, as the default
          String bindAddress = System.getProperty("gemfire.jg-bind-address");
          if (bindAddress != null && bindAddress.length() > 0) {
            ip_addr=InetAddress.getByName(bindAddress);
          }
          else {
            ip_addr=JChannel.getGfFunctions().getLocalHost();  // get first NIC found (on multi-homed systems)
          }
          size=-1; // GemStoneAddition
          setSize(size(version));
        }
        catch(Exception e) {
            if(log.isWarnEnabled()) log.warn("caught unexpected exception", e);
        }
    }

    public IpAddress(int port) {
        this.port=port;
        setAddressToLocalHost();
        JChannel.getGfFunctions().setDefaultGemFireAttributes(this); // GemStoneAddition
    }



    public final InetAddress  getIpAddress()               {return ip_addr;}
    public final int          getPort()                    {return port;}

    public final SocketAddress getSocketAddress() { // GemStoneAddition
      return new InetSocketAddress(ip_addr, port);
    }


    /** GemStoneAddition - cache the result of querying */
    private transient boolean isMcastAddr;
    private transient boolean isMcastAddrCached;
    
    public final boolean      isMulticastAddress() {
        if (!isMcastAddrCached) {
          isMcastAddr = ip_addr != null && ip_addr.isMulticastAddress();
          isMcastAddrCached = true;
        }
        return isMcastAddr;
    }

    /**
     * Returns the additional_data.
     * @return byte[]
     */
    public final byte[] getAdditionalData() {
        return additional_data;
    }

    /**
     * Sets the additional_data.
     * @param additional_data The additional_data to set
     */
    public final void setAdditionalData(byte[] additional_data) {
        this.additional_data = additional_data;
        size=-1; // GemStoneAddition
        setSize(size(version));
    }

    // GemStoneAddition
    public int getVmKind() {
      return this.vmKind;
    }

    // GemStoneAddition
    public void setVmKind(int vmKind) {
      this.vmKind = vmKind;
    }

    // GemStoneAddition
    public int getDirectPort() {
      return this.directPort;
    }

    // GemStoneAddition
    public void setDirectPort(int directPort) {
      this.directPort = directPort;
    }
    
    // GemStoneAddition
    public int getBirthViewId() {
      return this.birthViewId;
    }
    
    // GemStoneAddition
    public void setBirthViewId(long vid) {
      this.birthViewId = (int)(vid & Integer.MAX_VALUE);
    }

    // GemStoneAddition
    public final short getVersionOrdinal() {
      return this.version;
    }

    // GemStoneAddition
    public final void setVersionOrdinal(short version) {
      this.version = version;
    }
    
    // GemStoneAddition
    public void setName(String v) {
      if (name == null) {
        this.name = "";
      } else {
        this.name = v;
      }
    }

    /**
     * Establishes an order between 2 addresses. Assumes other contains non-null IpAddress.
     * Excludes channel_name from comparison.
     * @return 0 for equality, value less than 0 if smaller, greater than 0 if greater.
     */
    public final int compare(IpAddress other) {
        return compareTo(other);
    }


    /**
     * implements the java.lang.Comparable interface
     * @see java.lang.Comparable
     * @param o - the Object to be compared
     * @return a negative integer, zero, or a positive integer as this object is less than,
     *         equal to, or greater than the specified object.
     * @exception java.lang.ClassCastException - if the specified object's type prevents it
     *            from being compared to this Object.
     */
    public final int compareTo(Object o) {
//        int   h1, h2, rc; // added Nov 7 2005, makes sense with canonical addresses

        if(this == o) return 0;
        if ((o == null) || !(o instanceof IpAddress))
            throw new ClassCastException("comparison between different classes: the other object is " +
                    (o != null? o.getClass() : o));
        IpAddress other = (IpAddress) o;
        if(ip_addr == null)
            if (other.ip_addr == null) return port < other.port ? -1 : (port > other.port ? 1 : 0);
            else return -1;
      
        // GemStoneAddition - use ipAddress bytes instead of hash, which is really a hash in Ipv6 addresses
//        h1=ip_addr.hashCode();
//        h2=other.ip_addr.hashCode();
//        rc=h1 < h2? -1 : h1 > h2? 1 : 0;
        byte[] myBytes = ip_addr.getAddress();
        byte[] otherBytes = other.ip_addr.getAddress();

        if (myBytes != otherBytes) {
          for (int i = 0; i < myBytes.length; i++) {
            if (i >= otherBytes.length)
              return -1; // same as far as they go, but shorter...
            if (myBytes[i] < otherBytes[i])
              return -1;
            if (myBytes[i] > otherBytes[i])
              return 1;
          }
          if (myBytes.length > otherBytes.length)
            return 1; // same as far as they go, but longer...
        }
        int comp = ((port < other.port) ? -1
                      : (port > other.port ? 1 : 0));
        // GemStoneAddition - bug #41983, address of kill-9'd member is reused
        // before it can be ejected from membership
        if (comp == 0) {
          if (this.birthViewId >= 0 && other.birthViewId >= 0) {
            if (this.birthViewId < other.birthViewId) {
              comp = -1;
            } else if (other.birthViewId < this.birthViewId) {
              comp = 1;
            }
          } else if (this.processId != 0 && other.processId != 0) {
            // starting in 8.0 we also consider the processId.  During startup
            // we may have a message from a member that hasn't finished joining
            // and address canonicalization may find an old address that has
            // the same addr:port.  Since the new member doesn't have a viewId
            // its address will be equal to the old member's address unless
            // we also pay attention to the processId.
            if (this.processId < other.processId){
              comp = -1;
            } else if (other.processId < this.processId) {
              comp = 1;
            }
          }
        }
        return comp;
    }



    @Override // GemStoneAddition
    public final boolean equals(Object obj) {
        if(this == obj) return true; // added Nov 7 2005, makes sense with canonical addresses
        if(obj == null) return false;
        if (!(obj instanceof IpAddress)) return false; // GemStoneAddition
        return compareTo(obj) == 0 ? true : false;
    }




    @Override // GemStoneAddition
    public final int hashCode() {
        return ip_addr != null ? ip_addr.hashCode() + port : port;
    }




    @Override // GemStoneAddition
    public String toString() {
        StringBuffer sb=new StringBuffer();

        if(ip_addr == null)
            sb.append("<null>");
        else {
            if(ip_addr.isMulticastAddress())
                sb.append(ip_addr.getHostAddress());
            else {
                String host_name=null;
                if(resolve_dns) // GemStoneAddition
                    host_name=JChannel.getGfFunctions().getHostName(ip_addr);
                else
                    host_name=ip_addr.getHostAddress();
                appendShortName(host_name, sb);
            }
        }
        // GemStoneAddition - name and process id 
        if (!"".equals(this.name) || processId > 0) {
          sb.append('(');
          if (!"".equals(this.name)) {
            sb.append(this.name);
            if (processId > 0) {
              sb.append(':');
            }
          }
          if (processId > 0) {
            sb.append(processId);
          }
          String vmKindStr = JChannel.getGfFunctions().getVmKindString(vmKind);
          sb.append(vmKindStr);
          sb.append(')');
        }
        // GemStoneAddition - coordinator inhibition

        if (this.splitBrainEnabled) {
          if (!this.shouldNotBeCoordinator) {
            sb.append("<ec>");
          }
        }

        if (this.birthViewId >= 0) {
          sb.append("<v" + this.birthViewId + ">");
        }
        if (this.version != JChannel.getGfFunctions().getCurrentVersionOrdinal()) {
          sb.append("(version:").append(this.version)
              .append(')');
        }

        /*        if (this.splitBrainEnabled) {
          sb.append("<sb>");
        }
*/
        sb.append(":" + port);
        //GemStoneAddition - don't print encoded additional_data
//         if(additional_data != null)
//             sb.append(" (additional data: ").append(additional_data.length).append(" bytes)");
        return sb.toString();
    }





    /**
     * Input: "daddy.nms.fnc.fujitsu.com", output: "daddy". Appends result to string buffer 'sb'.
     * @param hostname The hostname in long form. Guaranteed not to be null
     * @param sb The string buffer to which the result is to be appended
     */
    private void appendShortName(String hostname, StringBuffer sb) {
        if(hostname == null) return;
        int  index=hostname.indexOf('.');
        if(index > 0 && !Character.isDigit(hostname.charAt(0)))
            sb.append(hostname.substring(0, index));
        else
            sb.append(hostname);
    }


    public void writeExternal(ObjectOutput out) throws IOException {
      if(ip_addr != null) {
        byte[] address=ip_addr.getAddress();
        out.writeByte(address.length);
        out.write(address, 0, address.length);
      }
      else {
        out.writeByte(0);
      }
      out.writeShort(port & 0xffff);
      out.writeInt(processId); // GemStoneAddition
      out.writeInt(directPort);
      out.writeByte(vmKind);
      out.writeInt(this.birthViewId);
      out.writeUTF(getName());
      if(additional_data != null) {
          out.writeInt(additional_data.length);
          out.write(additional_data, 0, additional_data.length);
      }
      else {
          out.writeInt(0);
      }
      out.writeByte(getFlags()); // GemStoneAddition
      JGroupsVersion.writeOrdinal(out, this.version, true); // GemStoneAddition
    } 
    
    
    
    
    public void readExternal(ObjectInput in) throws IOException, ClassNotFoundException {
      int len=in.readUnsignedByte();
      if(len > 0) {
          byte[] a = new byte[len];
          in.readFully(a);
          this.ip_addr=InetAddress.getByAddress(a);
      }
      //then read the port
      port=in.readUnsignedShort();

      // GemStoneAddition - process id, etc
      processId = in.readInt();
      directPort = in.readInt();
      vmKind = in.readByte();
      birthViewId = in.readInt();
      name = in.readUTF();
      len=in.readInt();
      if(len > 0) {
          additional_data=new byte[len];
          in.readFully(additional_data, 0, additional_data.length);
      }
      int flags = in.readUnsignedByte(); // GemStoneAddition
      setFlags(flags);
      readVersion(flags, in); // GemStoneAddition
    }

    public void writeTo(DataOutputStream out) throws IOException {
        toData(out);
    }

    public void readFrom(DataInputStream in) throws IOException {
        fromData(in);
    }

  public byte getFlags() {
    // GemStoneAddition - flags
    int flags = 0;
    if (this.shouldNotBeCoordinator) {
      flags |= 0x1;
    }
    if (this.splitBrainEnabled) {
      flags |= 0x2;
    }
    // always add version to flags but allow for absence of this flag
    flags |= 0x4;
    return (byte)(flags & 0xff);
  }
  
  public void setFlags(int flags) {
    // GemStoneAddition - flags
    if ((flags & 0x1) == 0x1) {
      this.shouldNotBeCoordinator = true;
    }
    if ((flags & 0x2) == 0x2) {
      this.splitBrainEnabled = true;
    }
  }

  // GemStoneAddition - version
  public void readVersion(int flags, DataInput in) throws IOException {
    if ((flags & 0x4) == 0x4) {
      this.version = JGroupsVersion.readOrdinal(in);
      if (this.version == 0) {
        this.version = JChannel.getGfFunctions().getCurrentVersionOrdinal();
      }
    }
  }

    // GemStoneAddition - dataserializable
    public int getDSFID() {
      return IP_ADDRESS;
    }

    // GemStoneAddition - dataserializable
    public void toData(DataOutput out) throws IOException {
      byte[] address;

      if(ip_addr != null) {
          address=ip_addr.getAddress();
          out.writeByte(address.length);
          out.write(address, 0, address.length);
      }
      else {
          out.writeByte(0);
      }
      out.writeShort(port);
      out.writeInt(processId); // GemStoneAddition
      out.writeInt(directPort);
      out.writeByte(vmKind);
      out.writeInt(birthViewId);
      out.writeUTF(getName());
      // for 6.x piggyback the weight in the roles array.  For 7.0 we will
      // need to add it as a field and, hopefully, add an extensible way to
      // add new attributes that an old version of the product can ignore.
      // The GossipServer FILE_FORMAT will need to be bumped when we do that.
//      out.writeByte(memberWeight);
      if (memberWeight > 0) {
        String[] forser;
        String[] roles = getRoles();
        forser = new String[roles.length+1];
        if (roles.length > 0) {
          System.arraycopy(roles, 0, forser, 0, roles.length);
        }
        forser[forser.length-1] = MEMBER_WEIGHT_PREFIX + memberWeight;
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        DataOutput dao = new DataOutputStream(baos);
        JChannel.getGfFunctions().writeStringArray(forser, dao);
        byte[] payload = baos.toByteArray();
        out.writeInt(payload.length);
        out.write(payload, 0, payload.length);
      } else {
        if(additional_data != null) {
          out.writeInt(additional_data.length);
          out.write(additional_data, 0, additional_data.length);
        }
        else {
          out.writeInt(0);
        }
      }
      out.writeByte(getFlags());
      JGroupsVersion.writeOrdinal(out, this.version, true);
  }

  // GemStoneAddition - dataserializable
  public void fromData(DataInput in) throws IOException {
      int len=in.readUnsignedByte();
      if(len > 0) {
          byte[] a = new byte[len];
          in.readFully(a);
          this.ip_addr=InetAddress.getByAddress(a);
      }
      port=in.readUnsignedShort();
      processId = in.readInt(); // GemStoneAddition
      directPort = in.readInt();
      vmKind = in.readByte();
      birthViewId = in.readInt();
      name = in.readUTF();
      len=in.readInt();
      if(len > 0) {
          additional_data=new byte[len];
          in.readFully(additional_data, 0, additional_data.length);
          String roles[] = getRoles();
          int lastIndex = roles.length-1;
          int numValidRoles = lastIndex;
          if (roles.length > 0 && roles[lastIndex].startsWith(MEMBER_WEIGHT_PREFIX)) {
            String weightString = roles[lastIndex].substring(MEMBER_WEIGHT_PREFIX.length());
            memberWeight = Byte.parseByte(weightString);
            String[] newroles = new String[numValidRoles];
            System.arraycopy(roles, 0, newroles, 0, numValidRoles);
            setRoles(newroles);
          }
      }
      // GemStoneAddition - flags
      int flags = in.readUnsignedByte();
      setFlags(flags);
      // GemStoneAddition - version
      readVersion(flags, in);
  }

    // GemStoneAddition - for ack processing we don't need the whole
    // address
    byte[] cachedAddress;
    public void toDataShort(DataOutput out) throws IOException {
      byte[] address;

      if (cachedAddress != null)
        address = cachedAddress;
      else {
        address=ip_addr.getAddress();
        cachedAddress = address;
      }
      out.writeByte(address.length);
      out.write(address, 0, address.length);
      out.writeShort(port);
  }

  // GemStoneAddition - for ack processing
  public void fromDataShort(DataInput in) throws IOException {
      int len=in.readUnsignedByte();
      //read the four bytes
      byte[] a = new byte[len];
      //in theory readFully(byte[]) should be faster
      //than read(byte[]) since latter reads
      // 4 bytes one at a time
      in.readFully(a);
      //look up an instance in the cache
      this.ip_addr=InetAddress.getByAddress(a);
      //then read the port
      port=in.readUnsignedShort();
  }

    public int size(short version) {
        if(size >= 0)
            return size;
        // address length
        int tmp_size = Global.BYTE_SIZE;
        // address
        if(ip_addr != null)
            tmp_size+=ip_addr.getAddress().length; // 4 bytes for IPv4
        // port
        tmp_size += Global.SHORT_SIZE;
        // PID
        tmp_size += Global.INT_SIZE; // GemStoneAddition
        // direct-port
        tmp_size += Global.INT_SIZE; // GemStoneAddition
        // vm-kind
        tmp_size += Global.BYTE_SIZE;
        // view-id
        tmp_size += Global.INT_SIZE; // GemStoneAddition
        // additional data size
        tmp_size += Global.INT_SIZE;
        // additional data
        if(additional_data != null)
            tmp_size+=additional_data.length;
        // flags
        tmp_size += Global.BYTE_SIZE;
        // version
        tmp_size += (this.version < 256? 1 : 3);
        // GemStoneAddition - ignore durableClientAttributes in size calculations
        // since client IDs are never used in datagram size estimations
        setSize(tmp_size);
        return tmp_size;
    }

    @Override // GemStoneAddition
    public Object clone() throws CloneNotSupportedException {
        IpAddress ret=new IpAddress(ip_addr, port);
        ret.processId = this.processId; // GemStoneAddition
        ret.shouldNotBeCoordinator = this.shouldNotBeCoordinator; // GemStoneAddition
        ret.splitBrainEnabled = this.splitBrainEnabled; // GemStoneAddition
        ret.name = this.name; // GemStoneAddition
        ret.version = this.version; // GemStoneAddition
        ret.birthViewId = this.birthViewId; // GemStoneAddition
        if(additional_data != null) {
            ret.additional_data=new byte[additional_data.length];
            System.arraycopy(additional_data, 0, ret.additional_data, 0, additional_data.length);
        }
        return ret;
    }

    @Override
    public short[] getSerializationVersions() {
       return null;
    }

    public void setBirthViewId(int birthViewId) {
      this.birthViewId = birthViewId;
    }

    public int getSize() {
      return size;
    }

    public void setSize(int size) {
      this.size = size;
    }


}
