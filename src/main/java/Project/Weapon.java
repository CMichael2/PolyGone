package Project;

import java.awt.event.KeyEvent;

public class Weapon {
    PolyGone game;
    Player player;

    public double bulletSpeed = 12.0; //change to determine bullet speed
    public long shotCooldown = 50; //change to determine the firing rate/delay in milliseconds
    public long lastShotTime = 0;
    public double bulletDamage = 1;
    public int maxAmmo = 50; //sets max ammo
    public int currentAmmo = maxAmmo;
    public long ammoReloadCooldown = 3000; //sets ammo bar reload time
    public long lastAmmoRegenTime = 0; //do not change
    public boolean isReloading = false;
    public double range;

    public String weaponName;

    public Weapon(PolyGone game, Player player, double bulletSpeed, long shotCooldown, double bulletDamage, int maxAmmo, long reloadTime, double range, String weaponName) {
        this.game = game;
        this.player = player;
        this.bulletSpeed = bulletSpeed;
        this.shotCooldown = shotCooldown;
        this.bulletDamage = bulletDamage;
        this.maxAmmo = maxAmmo;
        this.currentAmmo = maxAmmo;
        this.ammoReloadCooldown = reloadTime;
        this.weaponName = weaponName;
        this.range = range;
    }
    public void updateAmmoRegen() {
        if ((currentAmmo == 0 || game.isKeyPressed(KeyEvent.VK_R)) && !isReloading && currentAmmo!=maxAmmo) { //only regens ammo when needed
            isReloading = true;
            lastAmmoRegenTime = System.currentTimeMillis();
        }
        if (isReloading) {
            long currentTime = System.currentTimeMillis();
            if (currentTime - lastAmmoRegenTime >= ammoReloadCooldown) { //waits till the regeneration time is over to regen ammo
                currentAmmo = maxAmmo;
                isReloading = false;
            }
        }
    }



}
