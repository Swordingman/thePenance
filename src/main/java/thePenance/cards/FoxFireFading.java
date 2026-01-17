package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.utility.NewQueueCardAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.SlowPower;
import thePenance.PenanceMod;
import thePenance.util.CardStats;

public class FoxFireFading extends BaseCard {

    public static final String ID = PenanceMod.makeID("FoxFireFading");
    private static final int COST = 1;
    private static final int MAGIC = 2; // 缓慢层数
    private static final int UPG_MAGIC = -1; // 2->1 (注意这是减少层数)

    public FoxFireFading() {
        super(ID, new CardStats(
                CardColor.CURSE,
                CardType.CURSE,
                CardRarity.SPECIAL,
                CardTarget.ALL,
                COST
        ));
        // 这里基础值是2，升级后增加-1，即变为1
        setMagic(MAGIC, UPG_MAGIC);
        setExhaust(true);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 对玩家施加缓慢
        addToBot(new ApplyPowerAction(p, p, new SlowPower(p, magicNumber), magicNumber));

        // 对所有敌人施加缓慢
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!mo.isDeadOrEscaped()) {
                addToBot(new ApplyPowerAction(mo, p, new SlowPower(mo, magicNumber), magicNumber));
            }
        }
    }

    @Override
    public void triggerWhenDrawn() {
        addToBot(new NewQueueCardAction(this, true, false, true));
    }
}