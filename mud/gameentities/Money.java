package gameentities;

public class Money {
	
	public int amount;
	
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
