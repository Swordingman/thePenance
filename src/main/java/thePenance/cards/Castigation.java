package thePenance.cards;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.AbstractPower;
import thePenance.character.Penance;
import thePenance.powers.BarrierPower;
import thePenance.util.CardStats;

public class Castigation extends BaseCard {

    public static final String ID = makeID("Castigation");

    // 基础数值定义
    private static final int COST = 1;
    private static final int DAMAGE = 5;
    private static final int UPG_DAMAGE = 2;

    // 屏障的基础数值
    private static final int BARRIER = 4;
    private static final int UPG_BARRIER = 2;

    public Castigation() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.COMMON,
                CardTarget.ENEMY,
                COST
        ));

        // 1. 设置伤害 (受力量加成是攻击牌的默认行为，不用管)
        setDamage(DAMAGE, UPG_DAMAGE);

        // 2. 设置自定义变量 "Barrier" (屏障)
        // 参数含义: 键名, 类型(MAGIC即不受益于普通攻防计算), 基础值, 升级增量, 计算函数(PreCalc)
        setCustomVar("Barrier", VariableType.MAGIC, BARRIER, UPG_BARRIER, (card, monster, baseVal) -> {
            AbstractPlayer p = AbstractDungeon.player;
            AbstractPower strength = p.getPower("Strength");

            // 如果玩家有力量，数值 = 基础值 + 力量层数
            if (strength != null) {
                return baseVal + strength.amount;
            }
            // 否则返回原基础值
            return baseVal;
        });
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 造成伤害
        addToBot(new DamageAction(m, new DamageInfo(p, damage, damageTypeForTurn), AbstractGameAction.AttackEffect.BLUNT_LIGHT));

        // 3. 获得屏障
        // 这里不能再用 magicNumber 了，要读取我们在上面定义的 customVar
        int barrierAmount = customVar("Barrier");
        addToBot(new ApplyPowerAction(p, p, new BarrierPower(p, barrierAmount), barrierAmount));
    }
}