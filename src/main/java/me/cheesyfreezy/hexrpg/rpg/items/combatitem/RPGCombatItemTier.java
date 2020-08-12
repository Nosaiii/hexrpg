package me.cheesyfreezy.hexrpg.rpg.items.combatitem;

public enum RPGCombatItemTier {
	COMMON(1), DECENT(2), ORDINARY(3), RARE(4), GRAND(5);
	
	private final int tierLevel;
	RPGCombatItemTier(int tierLevel) {
		this.tierLevel = tierLevel;
	}
	
	public int getTierLevel() {
		return tierLevel;
	}
}