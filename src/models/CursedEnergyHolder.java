package models;

/**
 * Общий интерфейс для всех существ, использующих Проклятую Энергию (CE).
 * Устраняет дублирующие instanceof-цепочки в BattleManager и UnitInfoPanel.
 */
public interface CursedEnergyHolder {
    int getCursedEnergy();
    int getMaxCursedEnergy();
    boolean useCursedEnergy(int amount);
}
