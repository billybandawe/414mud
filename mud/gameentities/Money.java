package gameentities;

public class Money extends Object {

	public int amount;

	public Money() {
		super();
		amount = 1;
		line = "one dollar is sitting on the ground";
		/*name.add("money");
		name.add("dollar");*/
		name = "money";
	}

	public Money(int amount) {
		this.amount = amount;
	}
	
	public void AddMoney(int amountToAdd) {
		this.amount += amountToAdd;
	}
	
	public void SubtractMoney(int amountToRemove) {
		this.amount -= amountToRemove;
	}
	
	public int GetAmount() {
		return this.amount;
	}

}
