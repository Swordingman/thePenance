package thePenance.events;

import com.megacrit.cardcrawl.cards.AbstractCard;
import com.megacrit.cardcrawl.core.CardCrawlGame;
import com.megacrit.cardcrawl.core.Settings;
import com.megacrit.cardcrawl.dungeons.AbstractDungeon;
import com.megacrit.cardcrawl.events.AbstractImageEvent;
import com.megacrit.cardcrawl.localization.EventStrings;
import com.megacrit.cardcrawl.monsters.MonsterGroup;
import com.megacrit.cardcrawl.relics.AbstractRelic;
import com.megacrit.cardcrawl.rewards.RewardItem;
import com.megacrit.cardcrawl.rooms.AbstractRoom;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardAndObtainEffect;
import com.megacrit.cardcrawl.vfx.cardManip.ShowCardBrieflyEffect;
import thePenance.PenanceMod;
import thePenance.monsters.MafiaGodfather;
import thePenance.patches.PenanceHealPatches;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

public class CaseFile1184Event extends AbstractImageEvent {
    public static final String ID = PenanceMod.makeID("CaseFile1184Event");
    private static final EventStrings eventStrings = CardCrawlGame.languagePack.getEventString(ID);
    private static final String NAME = eventStrings.NAME;
    private static final String[] DESCRIPTIONS = eventStrings.DESCRIPTIONS;
    private static final String[] OPTIONS = eventStrings.OPTIONS;

    private int screenNum = 0;

    public CaseFile1184Event() {
        super(NAME, DESCRIPTIONS[0], "thePenance/images/events/case_file.jpg");

        // 选项1: 提交伪证
        this.imageEventText.setDialogOption(OPTIONS[0], new com.megacrit.cardcrawl.cards.curses.Doubt());
        // 选项2: 当庭释放
        this.imageEventText.setDialogOption(OPTIONS[1], new com.megacrit.cardcrawl.cards.curses.Writhe());
        // 选项3: 私下裁决 (战斗)
        this.imageEventText.setDialogOption(OPTIONS[2]);
    }

    @Override
    protected void buttonEffect(int buttonPressed) {
        switch (screenNum) {
            case 0:
                switch (buttonPressed) {
                    case 0: // 提交伪证
                        AbstractDungeon.player.gainGold(99);
                        upgradeRandomAttacks(2);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(
                                new com.megacrit.cardcrawl.cards.curses.Doubt(),
                                Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));

                        this.imageEventText.updateBodyText(DESCRIPTIONS[1]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[3]); // 离开
                        screenNum = 1;
                        break;

                    case 1: // 当庭释放
                        PenanceHealPatches.forceRealHeal(AbstractDungeon.player, AbstractDungeon.player.maxHealth);
                        AbstractDungeon.player.increaseMaxHp(5, true);
                        AbstractDungeon.effectList.add(new ShowCardAndObtainEffect(
                                new com.megacrit.cardcrawl.cards.curses.Writhe(),
                                Settings.WIDTH / 2.0F, Settings.HEIGHT / 2.0F));

                        this.imageEventText.updateBodyText(DESCRIPTIONS[2]);
                        this.imageEventText.clearAllDialogs();
                        this.imageEventText.setDialogOption(OPTIONS[3]); // 离开
                        screenNum = 1;
                        break;

                    case 2: // 私下裁决 (战斗)
                        screenNum = 2; // 标记为战斗状态

                        AbstractDungeon.getCurrRoom().monsters = new MonsterGroup(new MafiaGodfather(200.0F, 0.0F));
                        AbstractDungeon.getCurrRoom().eliteTrigger = true;

                        // 战斗开始时不要加奖励，会被系统清空
                        // 只是进入战斗
                        this.enterCombatFromImage();

                        // 保持ID一致
                        AbstractDungeon.lastCombatMetricKey = MafiaGodfather.ID;
                        break;
                }
                break;

            case 1: // 普通选项后的离开
            case 3: // 战斗后领完奖励的离开
                openMap();
                break;
        }
    }

    @Override
    public void reopen() {
        // 如果是从战斗(状态2)回来的
        if (screenNum == 2) {
            // 【核心修复】立刻上锁，变为状态3，防止无限循环
            screenNum = 3;

            // 1. 设置房间状态，必须设为 COMPLETE，否则有时候无法正常结束
            AbstractDungeon.getCurrRoom().phase = AbstractRoom.RoomPhase.COMPLETE;

            // 2. 重新添加奖励
            AbstractDungeon.getCurrRoom().rewards.clear();
            AbstractDungeon.getCurrRoom().addGoldToRewards(35);
            AbstractDungeon.getCurrRoom().addRelicToRewards(AbstractRelic.RelicTier.RARE);

            if (AbstractDungeon.player.hasRelic("Black Star")) {
                AbstractDungeon.getCurrRoom().addRelicToRewards(AbstractRelic.RelicTier.RARE);
            }

            AbstractDungeon.getCurrRoom().addCardReward(new RewardItem());

            // 3. 强制打开奖励界面
            AbstractDungeon.combatRewardScreen.open();

            // 4. 更新事件背景文字，并提供一个离开按钮作为兜底
            this.imageEventText.updateBodyText("清理完毕。");
            this.imageEventText.clearAllDialogs();
            this.imageEventText.setDialogOption(OPTIONS[3]);
        }
        // 如果已经是状态3，说明是领完奖励界面刷新，或者再次交互
        else if (screenNum == 3) {
            // 这时候才真正离开地图
            openMap();
        }
    }

    private void upgradeRandomAttacks(int num) {
        ArrayList<AbstractCard> attacks = new ArrayList<>();
        for (AbstractCard c : AbstractDungeon.player.masterDeck.group) {
            if (c.type == AbstractCard.CardType.ATTACK && c.canUpgrade()) {
                attacks.add(c);
            }
        }
        Collections.shuffle(attacks, new Random(AbstractDungeon.miscRng.randomLong()));
        for (int i = 0; i < Math.min(num, attacks.size()); i++) {
            attacks.get(i).upgrade();
            AbstractDungeon.player.bottledCardUpgradeCheck(attacks.get(i));
            AbstractDungeon.effectList.add(new ShowCardBrieflyEffect(attacks.get(i).makeStatEquivalentCopy()));
        }
    }
}