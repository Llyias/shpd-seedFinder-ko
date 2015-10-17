/*
 * Pixel Dungeon
 * Copyright (C) 2012-2015  Oleg Dolya
 *
 * Shattered Pixel Dungeon
 * Copyright (C) 2014-2015 Evan Debenham
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>
 */
package com.shatteredpixel.shatteredpixeldungeon.actors.mobs.npcs;

import java.util.ArrayList;
import java.util.Collection;

import com.shatteredpixel.shatteredpixeldungeon.Challenges;
import com.shatteredpixel.shatteredpixeldungeon.actors.hero.Hero;
import com.shatteredpixel.shatteredpixeldungeon.items.Generator;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CeremonialCandle;
import com.watabou.noosa.audio.Sample;
import com.shatteredpixel.shatteredpixeldungeon.Assets;
import com.shatteredpixel.shatteredpixeldungeon.Dungeon;
import com.shatteredpixel.shatteredpixeldungeon.Journal;
import com.shatteredpixel.shatteredpixeldungeon.actors.Actor;
import com.shatteredpixel.shatteredpixeldungeon.actors.Char;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.Blob;
import com.shatteredpixel.shatteredpixeldungeon.actors.blobs.ToxicGas;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Buff;
import com.shatteredpixel.shatteredpixeldungeon.actors.buffs.Roots;
import com.shatteredpixel.shatteredpixeldungeon.actors.mobs.Mob;
import com.shatteredpixel.shatteredpixeldungeon.effects.CellEmitter;
import com.shatteredpixel.shatteredpixeldungeon.effects.Speck;
import com.shatteredpixel.shatteredpixeldungeon.items.Heap;
import com.shatteredpixel.shatteredpixeldungeon.items.Item;
import com.shatteredpixel.shatteredpixeldungeon.items.potions.PotionOfStrength;
import com.shatteredpixel.shatteredpixeldungeon.items.quest.CorpseDust;
import com.shatteredpixel.shatteredpixeldungeon.items.wands.Wand;
import com.shatteredpixel.shatteredpixeldungeon.levels.PrisonLevel;
import com.shatteredpixel.shatteredpixeldungeon.levels.Room;
import com.shatteredpixel.shatteredpixeldungeon.levels.Terrain;
import com.shatteredpixel.shatteredpixeldungeon.plants.Plant;
import com.shatteredpixel.shatteredpixeldungeon.scenes.GameScene;
import com.shatteredpixel.shatteredpixeldungeon.sprites.ItemSpriteSheet;
import com.shatteredpixel.shatteredpixeldungeon.sprites.WandmakerSprite;
import com.shatteredpixel.shatteredpixeldungeon.utils.GLog;
import com.shatteredpixel.shatteredpixeldungeon.utils.Utils;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndQuest;
import com.shatteredpixel.shatteredpixeldungeon.windows.WndWandmaker;
import com.watabou.utils.Bundle;
import com.watabou.utils.Random;

public class Wandmaker extends NPC {

	{
		name = "old wandmaker";
		spriteClass = WandmakerSprite.class;
	}

	private static final String INTRO_WARRIOR	=
		"Oh, what a pleasant surprise to meet a hero in such a depressing place! " +
		"If you're up to helping an old man out, I may have a task for you.\n\n";

	private static final String INTRO_ROGUE		=
		"Oh Goodness, you startled me! I haven't met a bandit from this place that still has his sanity, " +
		"so you must be from the surface! If you're up to helping a stranger out, I may have a task for you.\n\n";

	private static final String INTRO_MAGE		=
		"Oh, hello %s! I heard there was some ruckus regarding you and the mage's institute? " +
		"Oh never mind, I never liked those stick-in-the-muds anyway. If you're willing, I may have a task for you.\n\n";

	private static final String INTRO_HUNTRESS	=
		"Oh, hello miss! A friendly face is a pleasant surprise down here isn't it? " +
		"In fact, I swear I've seen your face before, but I can't put my finger on it... " +
		"Oh never mind, if you're here for adventure, I may have a task for you.\n\n";

	private static final String INTRO_2 	=
		"I came here to find a rare ingredient to use in wandmaking, but I've gotten myself lost, " +
		"and my magical shield is weakening. I'll need to leave soon, but can't bear to go without getting what I came for.\n\n";


	private static final String INTRO_DUST	=
		"I'm looking for some corpse dust. It's a special kind of cursed bone meal that usually shows up in places like this. " +
				"There should be a barricaded room around here somewhere, I'm sure some dust will turn up there. " +
				"Do be careful though, the curse the dust carries is quite potent, get back to me as fast as you can and I'll cleanse it for you.\n\n";

	private static final String INTRO_EMBER	=
		"I'm looking for some fresh embers from a newborn fire elemental. Elementals usually pop up when a summoning ritual isn't controlled, " +
				"so just find some candles and a ritual site and I'm sure you can get one to pop up. " +
				"You might want to keep some sort of freezing item handy though, elementals are very powerful, but ice will take them down quite easily.\n\n";

	private static final String INTRO_BERRY	=
		"The old warden of this prison kept a rotberry plant, and I'm after one of its seeds. The plant has probably gone wild by now though, " +
				"so getting it to give up a seed might be tricky. Its garden should be somewhere around here. " +
				"Try to keep away from its vine lashers if you want to stay in one piece. Using fire might be tempting but please don't, you'll kill the plant and destroy its seeds.\n\n";

	private static final String INTRO_4		=
			"If you can get that for me, I'll be happy to pay you with one of my finely crafted wands! " +
			"I brought two with me, so you can take whichever one you prefer.";

	//TODO
	private static final String REMINDER_DUST	=
		"Any luck with corpse dust, %s? Bone piles are the most obvious places to look.";

	private static final String REMINDER_EMBER	=
		"";
	
	private static final String REMINDER_BERRY	=
		"Any luck with a Rotberry seed, %s? No? Don't worry, I'm not in a hurry.";

	
	@Override
	protected boolean act() {
		throwItem();
		return super.act();
	}
	
	@Override
	public int defenseSkill( Char enemy ) {
		return 1000;
	}
	
	@Override
	public String defenseVerb() {
		return "absorbed";
	}
	
	@Override
	public void damage( int dmg, Object src ) {
	}
	
	@Override
	public void add( Buff buff ) {
	}
	
	@Override
	public boolean reset() {
		return true;
	}
	
	@Override
	public void interact() {
		
		sprite.turnTo( pos, Dungeon.hero.pos );
		if (Quest.given) {
			
			Item item;
			switch (Quest.type) {
				case 1:
				default:
					item = Dungeon.hero.belongings.getItem(CorpseDust.class);
					break;
				case 2:
					item = Dungeon.hero.belongings.getItem(CorpseDust.class); //TODO: elemental embers
					break;
				case 3:
					item = Dungeon.hero.belongings.getItem(Rotberry.Seed.class);
					break;
			}

			if (item != null) {
				GameScene.show( new WndWandmaker( this, item ) );
			} else {
				tell( "not yet", Dungeon.hero.givenName() ); //TODO
			}
			
		} else {

			String msg = "";
			switch(Dungeon.hero.heroClass){
				case WARRIOR:
					msg += INTRO_WARRIOR;
					break;
				case ROGUE:
					msg += INTRO_ROGUE;
					break;
				case MAGE:
					msg += INTRO_MAGE;
					break;
				case HUNTRESS:
					msg += INTRO_HUNTRESS;
					break;
			}

			msg += INTRO_2;

			switch (Quest.type){
				case 1:
					msg += INTRO_DUST;
					break;
				case 2:
					msg += INTRO_EMBER;
					break;
				case 3:
					msg += INTRO_BERRY;
					break;
			}

			msg += INTRO_4;

			tell(msg, Dungeon.hero.givenName()); //TODO probable want to make this 2 separate windows
			
			Journal.add( Journal.Feature.WANDMAKER );
			Quest.given = true;
		}
	}
	
	private void tell( String format, Object...args ) {
		GameScene.show( new WndQuest( this, Utils.format( format, args ) ) );
	}
	
	@Override
	public String description() {
		return
			"This old but hale gentleman wears a slightly confused " +
			"expression. He is protected by a magic shield.";
	}
	
	public static class Quest {

		private static int type;
		// 1 = corpse dust quest
		// 2 = elemental embers quest
		// 3 = rotberry quest
		
		private static boolean spawned;
		
		private static boolean given;
		
		public static Wand wand1;
		public static Wand wand2;
		
		public static void reset() {
			spawned = false;
			type = 0;

			wand1 = null;
			wand2 = null;
		}
		
		private static final String NODE		= "wandmaker";
		
		private static final String SPAWNED		= "spawned";
		private static final String TYPE		= "type";
		private static final String GIVEN		= "given";
		private static final String WAND1		= "wand1";
		private static final String WAND2		= "wand2";

		private static final String RITUALPOS	= "ritualpos";
		
		public static void storeInBundle( Bundle bundle ) {
			
			Bundle node = new Bundle();
			
			node.put( SPAWNED, spawned );
			
			if (spawned) {
				
				node.put( TYPE, type );
				
				node.put( GIVEN, given );
				
				node.put( WAND1, wand1 );
				node.put(WAND2, wand2);

				if (type == 2){
					node.put( RITUALPOS, CeremonialCandle.ritualPos );
				}

			}
			
			bundle.put( NODE, node );
		}
		
		public static void restoreFromBundle( Bundle bundle ) {

			Bundle node = bundle.getBundle( NODE );
			
			if (!node.isNull() && (spawned = node.getBoolean( SPAWNED ))) {

				//TODO remove when pre-0.3.2 saves are no longer supported
				if (bundle.contains(TYPE)) {
					type = node.getInt(TYPE);
				} else {
					type = node.getBoolean("alternative")? 1 : 3;
				}
				
				given = node.getBoolean( GIVEN );
				
				wand1 = (Wand)node.get( WAND1 );
				wand2 = (Wand)node.get( WAND2 );

				if (type == 2){
					CeremonialCandle.ritualPos = bundle.getInt( RITUALPOS );
				}

			} else {
				reset();
			}
		}
		
		public static boolean spawn( PrisonLevel level, Room room, Collection<Room> rooms ) {
			if (!spawned && Dungeon.depth > 6 && Random.Int( 10 - Dungeon.depth ) == 0
					|| type != 0) {
				// decide between 1,2, or 3 for quest type.
				// but if the no herbalism challenge is enabled, only pick 1 or 2, no rotberry.
				if (type == 0) type = Random.Int(Dungeon.isChallenged(Challenges.NO_HERBALISM) ? 2 : 3)+1;

				//note that we set the type but can fail here. This ensures that if a level needs to be re-generated
				//we don't re-roll the quest, it will try to assign itself to that new level with the same type.
				if (setRoom( rooms )){
					Wandmaker npc = new Wandmaker();
					do {
						npc.pos = room.random();
					} while (level.map[npc.pos] == Terrain.ENTRANCE || level.map[npc.pos] == Terrain.SIGN);
					level.mobs.add( npc );

					spawned = true;

					given = false;
					wand1 = (Wand) Generator.random(Generator.Category.WAND);
					wand1.upgrade();

					do {
						wand2 = (Wand) Generator.random(Generator.Category.WAND);
					} while (wand2.getClass().equals(wand1.getClass()));
					wand2.upgrade();

					return true;
				}
			}
			return false;
		}
		
		private static boolean setRoom( Collection<Room> rooms) {
			Room questRoom = null;
			for (Room r : rooms){
				if (r.type == Room.Type.STANDARD && r.width() > 5 && r.height() > 5){
					if (type == 2 || r.connected.size() == 1){
						questRoom = r;
						break;
					}
				}
			}

			if (questRoom == null){
				return false;
			}

			switch (type){
				case 1: default:
					questRoom.type = Room.Type.MASS_GRAVE;
					break;
				case 2:
					questRoom.type = Room.Type.RITUAL_SITE;
					break;
				case 3:
					questRoom.type = Room.Type.ROT_GARDEN;
					break;
			}

			return true;
		}
		
		public static void complete() {
			wand1 = null;
			wand2 = null;
			
			Journal.remove( Journal.Feature.WANDMAKER );
		}
	}
	
	public static class Rotberry extends Plant {
		
		private static final String TXT_DESC =
			"Berries of this shrub taste like sweet, sweet death.";
		
		{
			image = 7;
			plantName = "Rotberry";
		}
		
		@Override
		public void activate() {
			Char ch = Actor.findChar(pos);
			
			GameScene.add( Blob.seed( pos, 100, ToxicGas.class ) );
			
			Dungeon.level.drop( new Seed(), pos ).sprite.drop();
			
			if (ch != null) {
				Buff.prolong( ch, Roots.class, TICK * 3 );
			}
		}
		
		@Override
		public String desc() {
			return TXT_DESC;
		}
		
		public static class Seed extends Plant.Seed {
			{
				plantName = "Rotberry";
				
				name = "seed of " + plantName;
				image = ItemSpriteSheet.SEED_ROTBERRY;
				
				plantClass = Rotberry.class;
				alchemyClass = PotionOfStrength.class;
			}
			
			@Override
			public boolean doPickUp( Hero hero ) {
				if (super.doPickUp(hero)) {
					
					if (Dungeon.level != null) {
						for (Mob mob : Dungeon.level.mobs) {
							mob.beckon( Dungeon.hero.pos );
						}
						
						GLog.w( "The seed emits a roar that echoes throughout the dungeon!" );
						CellEmitter.center( Dungeon.hero.pos ).start( Speck.factory( Speck.SCREAM ), 0.3f, 3 );
						Sample.INSTANCE.play( Assets.SND_CHALLENGE );
					}
					
					return true;
				} else {
					return false;
				}
			}
			
			@Override
			public String desc() {
				return TXT_DESC;
			}
		}
	}
}
