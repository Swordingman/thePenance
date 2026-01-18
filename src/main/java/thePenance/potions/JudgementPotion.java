package thePenance.potions;

import com.badlogic.gdx.graphics.Color;
import com.megacrit.cardcrawl.actions.common.ApplyPowerAction;
import com.megacrit.cardcrawl.core.AbstractCreature;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import thePenance.PenanceMod;
import thePenance.powers.JudgementPower;

public class JudgementPotion extends BasePotion {

    public static final String ID = PenanceMod.makeID("JudgementPotion");

    // 基础数值：5层
    private static final int POTENCY = 5;

    public JudgementPotion() {
        super(
                ID,
                POTENCY,
                PotionRarity.UNCOMMON, // 稀有度：罕见 (原版液体古铜给3层荆棘是罕见，这给5层裁决建议设为罕见或普通)
                PotionSize.SPIKY,      // 形状：尖刺形 (符合反伤/攻击性的直觉)
                Color.GOLD.cpy(),      // 液体：金色
                Color.ORANGE.cpy(),    // 混合：橙色
                null                   // 斑点：无
        );
    }

    @Override
    public void use(AbstractCreature target) {
        // 目标强制为玩家
        target = AbstractDungeon.player;

        // 战斗内生效
        if (AbstractDungeon.getCurrRoom().phase == AbstractRoom.RoomPhase.COMBAT) {
            this.addToBot(new ApplyPowerAction(target, target, new JudgementPower(target, this.potency), this.potency));
        }
    }

    @Override
    public String getDescription() {
        // 对应 JSON: "获得 #b", " 层 #y裁决 。"
        return DESCRIPTIONS[0] + this.potency + DESCRIPTIONS[1];
    }
}