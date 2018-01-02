package OO_8_Taxiplus;

public interface TwoWayIterator {
	Object next() throws Exception;
	Object previous() throws Exception;
	boolean hasNext();
	boolean hasPrevious();

}
