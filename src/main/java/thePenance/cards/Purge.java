package thePenance.cards;

import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import thePenance.actions.PurgeAction; // 假设动作在这个包
import thePenance.character.Penance;
import thePenance.util.CardStats;

public class Purge extends BaseCard {

    public static final String ID = makeID("Purge");

    private static final int COST = 2;
    private static final int DAMAGE = 5;
    private static final int UPGRADE_PLUS_DMG = 3;

    public Purge() {
        super(ID, new CardStats(
                Penance.Meta.CARD_COLOR,
                CardType.ATTACK,
                CardRarity.COMMON,
                CardTarget.ALL_ENEMY, // 目标是所有敌人
                COST
        ));

        setDamage(DAMAGE, UPGRADE_PLUS_DMG);

        // 重要：群体攻击必须开启这个开关
        this.isMultiDamage = true;
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 使用 multiDamage 变量，它是父类自动计算好的数组
        addToBot(new PurgeAction(p, this.multiDamage, this.damageTypeForTurn));
    }
}