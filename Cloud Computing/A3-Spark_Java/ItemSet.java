package A3;

import java.util.ArrayList;
import java.util.List;
import java.io.Serializable;
import java.util.Objects;

public class ItemSet implements Serializable {

	public ArrayList<Integer> items;

	// empty ItemSet
	public ItemSet() {
		this.items = new ArrayList<>();
	}

	// ItemSet from an item
	public ItemSet(Integer item) {
		this.items = new ArrayList<>();
		this.items.add(item);
	}

	// ItemSet from list of items
	public ItemSet(ArrayList<Integer> itemList) {
		this.items = itemList;
	}

	@Override
	public boolean equals(Object obj) {
		System.out.println("equals" + toString());
		if (obj == null) {
			return false;
		}

		if (obj == this) {
			return true;
		}

		if (obj.getClass() != getClass()) {
			return false;
		}

		ItemSet rhs = (ItemSet) obj;
		return Objects.equals(items, rhs.items);

		// Collections.sort(items);
  //   	Collections.sort(rhs.items); 
		// return rhs.items.equals(items) && 
		// 		rhs.numberOfTransactions == numberOfTransactions;
	}

    @Override
    public int hashCode() {
    	System.out.println("hashCode" + toString());
    	return Objects.hash(items);
    	// return Objects.hash(items, numberOfTransactions);

        // int result = 17;
        // result = 31 * result + items.hashCode();
        // result = 31 * result + numberOfTransactions;
        // return result;
    }

	public String toString() {
		StringBuilder sb = new StringBuilder();
		String delim = "";		
		sb.append("{");
		for(int item: this.items){
			sb.append(delim).append(item);
			delim = ",";
		}
		sb.append("}");
		return sb.toString();		
	}    


}