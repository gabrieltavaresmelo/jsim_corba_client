package sim;

/**
* sim/AnonymIDHolder.java .
* Generated by the IDL-to-Java compiler (portable), version "3.2"
* from sim.idl
* Segunda-feira, 9 de Dezembro de 2013 18h11min56s BRT
*/

public final class AnonymIDHolder implements org.omg.CORBA.portable.Streamable
{
  public sim.AnonymID value = null;

  public AnonymIDHolder ()
  {
  }

  public AnonymIDHolder (sim.AnonymID initialValue)
  {
    value = initialValue;
  }

  public void _read (org.omg.CORBA.portable.InputStream i)
  {
    value = sim.AnonymIDHelper.read (i);
  }

  public void _write (org.omg.CORBA.portable.OutputStream o)
  {
    sim.AnonymIDHelper.write (o, value);
  }

  public org.omg.CORBA.TypeCode _type ()
  {
    return sim.AnonymIDHelper.type ();
  }

}
