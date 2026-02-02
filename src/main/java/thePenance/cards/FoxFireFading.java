package thePenance.cards;

import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.characters.AbstractPlayer;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.SlowPower;
import thePenance.PenanceMod;
import thePenance.powers.LoseSlowPower; // 记得导入刚才创建的Power
import thePenance.util.CardStats;

public class FoxFireFading extends BaseCard {

    public static final String ID = PenanceMod.makeID("FoxFireFading");
    private static final int COST = 1;

    // 敌人的缓慢层数
    private static final int ENEMY_MAGIC = 2;
    private static final int UPG_ENEMY_MAGIC = 1;

    // 玩家的缓慢层数
    private static final int PLAYER_SLOW_AMT = 2;

    public FoxFireFading() {
        super(ID, new CardStats(
                CardColor.CURSE,
                CardType.CURSE,
                CardRarity.SPECIAL,
                CardTarget.ALL,
                COST
        ));
        setMagic(ENEMY_MAGIC, UPG_ENEMY_MAGIC);
        setExhaust(true);
        tags.add(PenanceMod.CURSE_OF_WOLVES);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 对玩家施加临时缓慢
        addToBot(new ApplyPowerAction(p, p, new SlowPower(p, PLAYER_SLOW_AMT), PLAYER_SLOW_AMT));
        // 添加移除能力：只要给 1 层 LoseSlowPower 即可触发移除逻辑
        addToBot(new ApplyPowerAction(p, p, new LoseSlowPower(p, 1), 1));

        // 2. 对所有敌人施加临时缓慢
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!mo.isDeadOrEscaped()) {
                addToBot(new ApplyPowerAction(mo, p, new SlowPower(mo, magicNumber), magicNumber));
                // 同样给敌人添加移除能力
                addToBot(new ApplyPowerAction(mo, p, new LoseSlowPower(mo, 1), 1));
            }
        }
    }

    @Override
    public void triggerWhenDrawn() {
        triggerWolfAutoplay();
    }
}