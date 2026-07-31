import { useState, useEffect, useRef, useCallback } from "react";
import { useNavigate } from "react-router-dom";
import {
  ArrowLeft,
  Play,
  Pause,
  SkipBack,
  SkipForward,
} from "lucide-react";

interface Chapter {
  index: number;
  title: string;
  src: string;
  preview: string;
}

const CHAPTERS: Chapter[] = [
  { index: 1, title: "Welcome", src: "/audio/narrator_rockscout_welcome_intro.mp3", preview: "Hey there. Welcome to RockScout." },
  { index: 2, title: "5-Source AI Rock ID", src: "/audio/narrator_rock_identification_voice.mp3", preview: "This is the big one. Tap Identify a Rock right on the home screen." },
  { index: 3, title: "Your Collection", src: "/audio/narrator_rocks_collection_guide.mp3", preview: "This is My Rocks — your personal collection." },
  { index: 4, title: "Field Tools", src: "/audio/narrator_field_capture_voice.mp3", preview: "Field Captures is where you log photos of rocks you find out in the field." },
  { index: 5, title: "Dig Sites & Gem Shows", src: "/audio/narrator_treasure_map_voice.mp3", preview: "This is your treasure map." },
  { index: 6, title: "Trip Planning", src: "/audio/narrator_trip_planner_voice.mp3", preview: "The Trip Planner is where you build your hunt route." },
  { index: 7, title: "Trading & Community", src: "/audio/narrator_trade_board_intro_voice.mp3", preview: "The Trade Board is where you post specimens to swap, sell, or trade." },
  { index: 8, title: "Social", src: "/audio/narrator_social_network_voice.mp3", preview: "RockScout's got a whole social network built in." },
  { index: 9, title: "Aurora & Night Sky", src: "/audio/narrator_aurora_forecaster_voice.mp3", preview: "This is your personal space weather station." },
  { index: 10, title: "Your Profile", src: "/audio/narrator_profile_level_up_voice.mp3", preview: "Tap your avatar to open your Profile — your Player Card." },
  { index: 11, title: "Reference Library", src: "/audio/narrator_periodic_table_voice_guide.mp3", preview: "The Periodic Table — all 118 elements." },
  { index: 12, title: "Artifacts & Wonders", src: "/audio/narrator_artifact_catalog_voice.mp3", preview: "The Artifacts tile takes you to a growing catalog of prehistoric artifacts." },
  { index: 13, title: "Field Kit", src: "/audio/narrator_rockhounding_guide_voice.mp3", preview: "The BLM Public Lands Guide breaks down rockhounding rules." },
  { index: 14, title: "Learn & Explore", src: "/audio/narrator_educational_guides_intro.mp3", preview: "The Educational Guides hub is where you go to learn the science." },
  { index: 15, title: "Premium & Free Tier", src: "/audio/narrator_pricing_explanation_voice.mp3", preview: "Let's talk about the money side of things." },
  { index: 16, title: "Outro", src: "/audio/narrator_rockscout_voice_intro.mp3", preview: "That's RockScout. I built it for rockhounders, because I am one." },
];

const TOTAL_CHAPTERS = CHAPTERS.length;

function formatTime(seconds: number): string {
  const min = Math.floor(seconds / 60);
  const sec = Math.floor(seconds % 60);
  return `${min}:${sec.toString().padStart(2, "0")}`;
}

export default function Narrator() {
  const navigate = useNavigate();
  const audioRef = useRef<HTMLAudioElement>(null);
  const [currentIndex, setCurrentIndex] = useState(0);
  const [isPlaying, setIsPlaying] = useState(false);
  const [currentTime, setCurrentTime] = useState(0);
  const [duration, setDuration] = useState(0);
  const [isSeeking, setIsSeeking] = useState(false);

  // Restore from localStorage on mount
  useEffect(() => {
    const savedChapter = localStorage.getItem("narrator_chapter");
    const savedPosition = localStorage.getItem("narrator_position");
    if (savedChapter !== null) {
      const idx = parseInt(savedChapter, 10);
      if (idx >= 0 && idx < TOTAL_CHAPTERS) {
        setCurrentIndex(idx);
        const pos = parseFloat(savedPosition || "0");
        // We'll seek after the audio loads
        setTimeout(() => {
          if (audioRef.current && pos > 0) {
            audioRef.current.currentTime = pos;
            setCurrentTime(pos);
          }
        }, 500);
      }
    }
  }, []);

  // Save to localStorage periodically
  useEffect(() => {
    localStorage.setItem("narrator_chapter", String(currentIndex));
    localStorage.setItem("narrator_position", String(currentTime));
  }, [currentIndex, currentTime]);

  const loadChapter = useCallback((index: number, autoplay: boolean = true) => {
    if (index < 0 || index >= TOTAL_CHAPTERS) return;
    setCurrentIndex(index);
    setCurrentTime(0);
    if (audioRef.current) {
      audioRef.current.src = CHAPTERS[index].src;
      audioRef.current.load();
      if (autoplay) {
        audioRef.current.play().then(() => setIsPlaying(true)).catch(() => {});
      }
    }
  }, []);

  const togglePlay = () => {
    if (!audioRef.current) return;
    if (isPlaying) {
      audioRef.current.pause();
      setIsPlaying(false);
    } else {
      if (currentTime >= duration && duration > 0) {
        loadChapter(currentIndex);
      } else {
        audioRef.current.play().then(() => setIsPlaying(true)).catch(() => {});
      }
    }
  };

  const skipBack = () => {
    if (currentIndex > 0) loadChapter(currentIndex - 1);
  };

  const skipForward = () => {
    if (currentIndex < TOTAL_CHAPTERS - 1) loadChapter(currentIndex + 1);
  };

  const handleEnded = () => {
    setIsPlaying(false);
    if (currentIndex < TOTAL_CHAPTERS - 1) {
      loadChapter(currentIndex + 1);
    }
  };

  const handleSeek = (e: React.ChangeEvent<HTMLInputElement>) => {
    const val = parseFloat(e.target.value);
    setIsSeeking(true);
    setCurrentTime(val);
  };

  const handleSeekCommit = () => {
    if (audioRef.current) {
      audioRef.current.currentTime = currentTime;
    }
    setIsSeeking(false);
  };

  const totalRunningTime = formatTime(TOTAL_CHAPTERS * 35); // rough estimate ~35s per chapter

  return (
    <div className="flex min-h-screen flex-col">
      {/* Header */}
      <div className="flex items-center gap-3 border-b border-border bg-card/50 px-4 py-4">
        <button
          onClick={() => navigate(-1)}
          className="grid h-10 w-10 place-items-center rounded-lg border border-border bg-background hover:bg-muted"
        >
          <ArrowLeft className="h-5 w-5" />
        </button>
        <h1 className="font-display text-xl font-bold">Narrator</h1>
      </div>

      <div className="mx-auto w-full max-w-3xl px-4 py-6">
        {/* Total running time banner */}
        <div className="mb-4 flex items-center justify-between rounded-xl border border-primary/30 bg-primary/10 px-5 py-3">
          <span className="font-display text-sm font-bold text-primary">
            {TOTAL_CHAPTERS} Chapters
          </span>
          <span className="text-sm font-medium text-foreground">
            Total: ~{totalRunningTime}
          </span>
        </div>

        {/* Player controls */}
        <div className="mb-6 rounded-2xl border border-border bg-card p-5">
          <div className="mb-3">
            <p className="font-display text-lg font-bold text-foreground">
              {CHAPTERS[currentIndex].index}. {CHAPTERS[currentIndex].title}
            </p>
            <p className="mt-1 text-sm text-muted-foreground">
              {CHAPTERS[currentIndex].preview}
            </p>
          </div>

          {/* Seek slider */}
          <input
            type="range"
            min={0}
            max={duration || 1}
            step={0.1}
            value={currentTime}
            onChange={handleSeek}
            onMouseUp={handleSeekCommit}
            onTouchEnd={handleSeekCommit}
            className="w-full accent-primary"
          />
          <div className="mt-1 flex justify-between text-xs text-muted-foreground">
            <span>{formatTime(currentTime)}</span>
            <span>{formatTime(duration)}</span>
          </div>

          {/* Playback controls */}
          <div className="mt-4 flex items-center justify-center gap-6">
            <button
              onClick={skipBack}
              disabled={currentIndex === 0}
              className="grid h-12 w-12 place-items-center rounded-full border border-border bg-background disabled:opacity-40 hover:bg-muted"
            >
              <SkipBack className="h-6 w-6" />
            </button>
            <button
              onClick={togglePlay}
              className="grid h-16 w-16 place-items-center rounded-full bg-primary text-primary-foreground shadow-lg hover:scale-105 transition-transform"
            >
              {isPlaying ? <Pause className="h-8 w-8" /> : <Play className="h-8 w-8" />}
            </button>
            <button
              onClick={skipForward}
              disabled={currentIndex === TOTAL_CHAPTERS - 1}
              className="grid h-12 w-12 place-items-center rounded-full border border-border bg-background disabled:opacity-40 hover:bg-muted"
            >
              <SkipForward className="h-6 w-6" />
            </button>
          </div>
        </div>

        {/* Chapter list */}
        <div className="space-y-2">
          {CHAPTERS.map((chapter, idx) => (
            <button
              key={chapter.index}
              onClick={() => loadChapter(idx)}
              className={`flex w-full items-center gap-3 rounded-xl border p-3 text-left transition-all ${
                idx === currentIndex
                  ? "border-primary bg-primary/10"
                  : "border-border bg-card/50 hover:border-primary/40 hover:bg-card"
              }`}
            >
              <div
                className={`grid h-9 w-9 shrink-0 place-items-center rounded-full text-sm font-bold ${
                  idx === currentIndex
                    ? "bg-primary text-primary-foreground"
                    : "bg-muted text-muted-foreground"
                }`}
              >
                {isPlaying && idx === currentIndex ? (
                  <Pause className="h-4 w-4" />
                ) : (
                  chapter.index
                )}
              </div>
              <div className="min-w-0 flex-1">
                <p
                  className={`truncate text-sm font-semibold ${
                    idx === currentIndex ? "text-primary" : "text-foreground"
                  }`}
                >
                  {chapter.title}
                </p>
                <p className="truncate text-xs text-muted-foreground">
                  {chapter.preview}
                </p>
              </div>
            </button>
          ))}
        </div>
      </div>

      <audio
        ref={audioRef}
        onTimeUpdate={(e) => {
          if (!isSeeking) setCurrentTime(e.currentTarget.currentTime);
        }}
        onLoadedMetadata={(e) => setDuration(e.currentTarget.duration)}
        onEnded={handleEnded}
        onPause={() => setIsPlaying(false)}
        onPlay={() => setIsPlaying(true)}
        src={CHAPTERS[currentIndex].src}
      />
    </div>
  );
}
