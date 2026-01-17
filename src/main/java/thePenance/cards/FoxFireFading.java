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

    // 敌人的缓慢层数：基础2，升级+1 -> 3
    private static final int ENEMY_MAGIC = 2;
    private static final int UPG_ENEMY_MAGIC = 1;

    // 玩家的缓慢层数：固定2
    private static final int PLAYER_SLOW_AMT = 2;

    public FoxFireFading() {
        super(ID, new CardStats(
                CardColor.CURSE,
                CardType.CURSE,
                CardRarity.SPECIAL,
                CardTarget.ALL,
                COST
        ));
        // 设置魔法值，用于控制敌人的层数
        setMagic(ENEMY_MAGIC, UPG_ENEMY_MAGIC);
        setExhaust(true);
        tags.add(PenanceMod.CURSE_OF_WOLVES);
    }

    @Override
    public void use(AbstractPlayer p, AbstractMonster m) {
        // 1. 对玩家施加固定层数 (2层) 缓慢
        // 注意：原版缓慢对玩家可能无效，除非你的Mod修改了SlowPower逻辑或怪物AI
        addToBot(new ApplyPowerAction(p, p, new SlowPower(p, PLAYER_SLOW_AMT), PLAYER_SLOW_AMT));

        // 2. 对所有敌人施加 !M! 层 (2->3) 缓慢
        for (AbstractMonster mo : AbstractDungeon.getCurrRoom().monsters.monsters) {
            if (!mo.isDeadOrEscaped()) {
                addToBot(new ApplyPowerAction(mo, p, new SlowPower(mo, magicNumber), magicNumber));
            }
        }
    }

    @Override
    public void triggerWhenDrawn() {
        // 直接调用 BaseCard 里的通用方法
        triggerWolfAutoplay();
    }
}