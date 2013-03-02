package com.wallyatkins.mud.merc;

import com.wallyatkins.mud.merc.types.ApplyAffectType;
import com.wallyatkins.mud.merc.types.EquipmentWearLocation;
import com.wallyatkins.mud.merc.types.ItemExtraFlag;
import com.wallyatkins.mud.merc.types.ItemType;
import com.wallyatkins.mud.merc.types.MobileActor;
import com.wallyatkins.mud.merc.types.MobileAffectBits;
import com.wallyatkins.mud.merc.types.PlayerActionBits;
import com.wallyatkins.mud.merc.types.RoomFlags;
import com.wallyatkins.mud.merc.types.RoomSectorTypes;

public abstract class Handler {
		
	/**
	 * Retrieve a character's trusted level for permission checking.
	 * @param character
	 * @return
	 */
	public static int get_trust(MudCharacter character) {
		if (character.desc != null && character.desc.original != null) {
			character = character.desc.original;
		}
		
		if (character.trust != 0) {
			return character.trust;
		}
		
		if (character.IS_NPC() && character.level >= Globals.LEVEL_HERO) {
			return Globals.LEVEL_HERO - 1;
		} else {
			return character.level;
		}
	}
	
	/**
	 * Retrieve a character's age.
	 * @param character
	 * @return
	 */
	public static int get_age(MudCharacter character) {
		return 17 + (character.played + (int) (System.currentTimeMillis() - character.logon)) / 7200;
	}
	
	/**
	 * Retrieve character's current strength.
	 * @param character
	 * @return
	 */
	public static int get_curr_str(MudCharacter character) {
		int max;
		
		if (character.IS_NPC()) {
			return 13;
		}
		
		if (Constants.class_table[character.clazz].attr_prime.equals(ApplyAffectType.STRENGTH)) {
			max = 25;
		} else {
			max = 20;
		}
		
		return Macros.URANGE(3, character.pcdata.perm_str + character.pcdata.mod_str, max);
	}

	/**
	 * Retrieve character's current intelligence.
	 * @param character
	 * @return
	 */
	public static int get_curr_int(MudCharacter character) {
		int max;
		
		if (character.IS_NPC()) {
			return 13;
		}
		
		if (Constants.class_table[character.clazz].attr_prime.equals(ApplyAffectType.INTELLIGENCE)) {
			max = 25;
		} else {
			max = 20;
		}
		
		return Macros.URANGE(3, character.pcdata.perm_int + character.pcdata.mod_int, max);
	}

	/**
	 * Retrieve character's current wisdom.
	 * @param character
	 * @return
	 */
	public static int get_curr_wis(MudCharacter character) {
		int max;
		
		if (character.IS_NPC()) {
			return 13;
		}
		
		if (Constants.class_table[character.clazz].attr_prime.equals(ApplyAffectType.WISDOM)) {
			max = 25;
		} else {
			max = 20;
		}
		
		return Macros.URANGE(3, character.pcdata.perm_wis + character.pcdata.mod_wis, max);
	}

	/**
	 * Retrieve character's current dexterity.
	 * @param character
	 * @return
	 */
	public static int get_curr_dex(MudCharacter character) {
		int max;
		
		if (character.IS_NPC()) {
			return 13;
		}
		
		if (Constants.class_table[character.clazz].attr_prime.equals(ApplyAffectType.DEXTERITY)) {
			max = 25;
		} else {
			max = 20;
		}
		
		return Macros.URANGE(3, character.pcdata.perm_dex + character.pcdata.mod_dex, max);
	}

	/**
	 * Retrieve character's current constitution.
	 * @param character
	 * @return
	 */
	public static int get_curr_con(MudCharacter character) {
		int max;
		
		if (character.IS_NPC()) {
			return 13;
		}
		
		if (Constants.class_table[character.clazz].attr_prime.equals(ApplyAffectType.CONSTITUTION)) {
			max = 25;
		} else {
			max = 20;
		}
		
		return Macros.URANGE(3, character.pcdata.perm_con + character.pcdata.mod_con, max);
	}
	
	/**
	 * Retrieve a character's number of carry capacity.
	 * @param character
	 * @return
	 */
	public static int can_carry_n(MudCharacter character) {
		if (!character.IS_NPC() && character.level >= Globals.LEVEL_IMMORTAL) {
			return 1000;
		}
		
		if (!character.IS_NPC() && Macros.IS_SET(character.act.index(), MobileActor.PET.index())) {
			return 0;
		}
		
		return EquipmentWearLocation.MAX_WEAR.index() + 2 * get_curr_dex(character) / 3;
	}
	
	public static int can_carry_w(MudCharacter character) {
		if (!character.IS_NPC() && character.level >= Globals.LEVEL_IMMORTAL) {
			return 1000000;
		}
		
		if (!character.IS_NPC() && Macros.IS_SET(character.act.index(), MobileActor.PET.index())) {
			return 0;
		}
		
		return Constants.str_app[get_curr_str(character)].carry;
	}

	/**
	 * Find a piece of equipment on a character.
	 * @param character
	 * @param wield
	 * @return
	 */
	public static MudObject get_eq_char(MudCharacter character, EquipmentWearLocation wear) {
		MudObject object = null;
		for (object = character.carrying; object != null; object = object.next_content) {
			if (object.wear_loc.index() == wear.index()) {
				return object;
			}
		}
		return null;
	}
	
	/**
	 * True if room is dark.
	 * @param roomIndex
	 * @return
	 */
	public static boolean room_is_dark(RoomIndexData roomIndex) {
		if (roomIndex.light > 0) {
			return false;
		}
		
		if (Macros.IS_SET(roomIndex.room_flags, RoomFlags.DARK.index())) {
			return true;
		}
		
		if (roomIndex.sector_type == RoomSectorTypes.INSIDE.index() || roomIndex.sector_type == RoomSectorTypes.CITY.index()) {
			return false;
		}
		
		// weather_info
		if (Globals.weather_data.sunlight == Globals.SUN_SET || Globals.weather_data.sunlight == Globals.SUN_DARK)  {
			return true;
		}
		
		return false;
	}
	
	/**
	 * True if a character can see victim.
	 * @param character
	 * @param victim
	 * @return
	 */
	public static boolean can_see(MudCharacter character, MudCharacter victim) {
		if (character.equals(victim)) {
			return true;
		}
		
		if (!victim.IS_NPC() &&
				Macros.IS_SET(victim.act.index(), PlayerActionBits.WISINVIS.index()) &&
				get_trust(character) < get_trust(victim)) {
			return false;
		}
		
		if (!victim.IS_NPC() &&
				Macros.IS_SET(character.act.index(), PlayerActionBits.HOLYLIGHT.index())) {
			return true;
		}
		
		if (character.IS_AFFECTED(MobileAffectBits.BLIND.index())) {
			return false;
		}
		
		if (room_is_dark(character.in_room) &&
				!character.IS_AFFECTED(MobileAffectBits.INFRARED.index())) {
			return false;
		}
		
		if (victim.IS_AFFECTED(MobileAffectBits.INVISIBLE.index()) &&
				!character.IS_AFFECTED(MobileAffectBits.DETECT_INVIS.index())) {
			return false;
		}
		
		if (victim.IS_AFFECTED(MobileAffectBits.HIDE.index()) &&
				!character.IS_AFFECTED(MobileAffectBits.DETECT_HIDDEN.index()) &&
				victim.fighting == null &&
				character.IS_NPC() ? !victim.IS_NPC() : victim.IS_NPC()) {
			return false;
		}
		
		return true;
	}

	/**
	 * True if character can see object.
	 * @param character
	 * @param object
	 * @return
	 */
	public static boolean can_see_obj(MudCharacter character, MudObject object) {
		if (!character.IS_NPC() &&
				Macros.IS_SET(character.act.index(), PlayerActionBits.HOLYLIGHT.index())) {
			return true;
		}
		
		if (object.item_type.index() == ItemType.POTION.index()) {
			return true;
		}
		
		if (character.IS_AFFECTED(MobileAffectBits.BLIND.index())) {
			return false;
		}
		
		if (object.item_type.index() == ItemType.LIGHT.index() &&
				object.value[2] != 0) {
			return true;
		}
		
		if (room_is_dark(character.in_room) &&
				!character.IS_AFFECTED(MobileAffectBits.INFRARED.index())) {
			return false;
		}
		
		return true;
	}
	
	/**
	 * True if character can drop object.
	 * @param character
	 * @param object
	 * @return
	 */
	public static boolean can_drop_obj(MudCharacter character, MudObject object) {
		if (!Macros.IS_SET(object.extra_flags, ItemExtraFlag.NODROP.index())) {
			return true;
		}
		
		if (!character.IS_NPC() &&
				character.level >= Globals.LEVEL_IMMORTAL) {
			return true;
		}
		
		return false;
	}
	
}
