package thePenance.monsters;

import com.megacrit.cardcrawl.actions.AbstractGameAction;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.actions.common.DamageAction;
import com.megacrit.cardcrawl.cards.DamageInfo;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.localization.MonsterStrings;
import com.megacrit.cardcrawl.monsters.AbstractMonster;
import com.megacrit.cardcrawl.powers.MinionPower;
import thePenance.PenanceMod;

public class FamilyCleaner extends AbstractMonster {
    public static final String ID = PenanceMod.makeID("FamilyCleaner");
    private static final MonsterStrings monsterStrings = CardCrawlGame.languagePack.getMonsterStrings(ID);

    private static final int HP = 32;
    private static final int DMG_A = 12;
    private static final int DMG_B = 8;
    private static final int HITS_B = 2;

    public FamilyCleaner(float x, float y) {
        super(monsterStrings.NAME, ID, HP, 0.0F, 0.0F, 150.0F, 220.0F, null, x, y);
        // 这里设置为 NORMAL 即可，MinionPower 由老大赋予，或者由 usePreBattleAction 赋予
        this.type = EnemyType.NORMAL;
        this.loadAnimation("images/monsters/theBottom/angryGremlin/skeleton.atlas", "images/monsters/theBottom/angryGremlin/skeleton.json", 1.0F);
        this.state.setAnimation(0, "idle", true);
        this.damage.add(new DamageInfo(this, DMG_A));
        this.damage.add(new DamageInfo(this, DMG_B));
    }

    @Override
    public void takeTurn() {
        switch (this.nextMove) {
            case 1:
                addToBot(new DamageAction(AbstractDungeon.player, this.damage.get(0), AbstractGameAction.AttackEffect.SLASH_HORIZONTAL));
                break;
            case 2:
                for (int i = 0; i < HITS_B; i++) {
                    addToBot(new DamageAction(AbstractDungeon.player, this.damage.get(1), AbstractGameAction.AttackEffect.SLASH_VERTICAL));
                }
                break;
        }
        AbstractDungeon.actionManager.addToBottom(new com.megacrit.cardcrawl.actions.common.RollMoveAction(this));
    }

    @Override
    protected void getMove(int num) {
        if (num < 50) {
            this.setMove((byte)1, Intent.ATTACK, this.damage.get(0).base);
        } else {
            this.setMove((byte)2, Intent.ATTACK, this.damage.get(1).base, HITS_B, true);
        }
    }

    @Override
    public void usePreBattleAction() {
        // 保险起见保留这个，但对召唤物来说通常不触发，主要靠 MafiaGodfather 给
        AbstractDungeon.actionManager.addToBottom(new ApplyPowerAction(this, this, new MinionPower(this)));
    }
}