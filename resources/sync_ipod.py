import os
import shutil
from gpod import Database, CoverDB, itdb_track_new

def sync_ipod(ipod_mount, music_dir):

    # Load iPod database
    itdb = Database(ipod_mount)

    # Clear existing tracks (optional)
    for track in itdb.tracks:
        itdb.remove(track)

    # Add new tracks from the music directory
    for filename in os.listdir(music_dir):
        if filename.endswith(('.mp3', '.m4a', '.aac', '.wav')):
            track_path = os.path.join(music_dir, filename)
            track = itdb_track_new()
            track.filename = track_path
            track.title = os.path.splitext(filename)[0]

            itdb.add(track)

            # Copy the file to iPod Music directory
            dest_path = os.path.join(ipod_mount, 'iPod_Control/Music', filename)
            shutil.copy(track_path, dest_path)

    # Save the updated database
    itdb.write()
    print("Sync complete!")

if __name__ == "__main__":
    IPOD_MOUNT = "/media/ipod"  # Adjust based on your system
    MUSIC_DIR = sys.argv[1] if len(sys.argv) > 1 else raise Exception("Invalid arguments; missing path")

    sync_ipod(IPOD_MOUNT, MUSIC_DIR)
