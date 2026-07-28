/* eslint-disable */
// AUTO-GENERATED — DO NOT EDIT
// Run migrations to regenerate.

export type Json =
  | string
  | number
  | boolean
  | null
  | { [key: string]: Json | undefined }
  | Json[]

export type Database = {
  // Allows to automatically instantiate createClient with right options
  // instead of createClient<Database, { PostgrestVersion: 'XX' }>(URL, KEY)
  __InternalSupabase: {
    PostgrestVersion: "14.5"
  }
  public: {
    Tables: {
      artifact_embeddings: {
        Row: {
          artifact_id: string
          created_at: string
          embedding_model: string
          id: number
          image_url: string
          text_embedding: string | null
        }
        Insert: {
          artifact_id: string
          created_at?: string
          embedding_model?: string
          id?: never
          image_url: string
          text_embedding?: string | null
        }
        Update: {
          artifact_id?: string
          created_at?: string
          embedding_model?: string
          id?: never
          image_url?: string
          text_embedding?: string | null
        }
        Relationships: []
      }
      connections: {
        Row: {
          created_at: string
          user_a: string
          user_b: string
        }
        Insert: {
          created_at?: string
          user_a: string
          user_b: string
        }
        Update: {
          created_at?: string
          user_a?: string
          user_b?: string
        }
        Relationships: [
          {
            foreignKeyName: "connections_user_a_fkey"
            columns: ["user_a"]
            isOneToOne: false
            referencedRelation: "profiles"
            referencedColumns: ["id"]
          },
          {
            foreignKeyName: "connections_user_b_fkey"
            columns: ["user_b"]
            isOneToOne: false
            referencedRelation: "profiles"
            referencedColumns: ["id"]
          },
        ]
      }
      message_requests: {
        Row: {
          created_at: string
          id: string
          receiver_id: string
          sender_id: string
          status: string
        }
        Insert: {
          created_at?: string
          id?: string
          receiver_id: string
          sender_id: string
          status?: string
        }
        Update: {
          created_at?: string
          id?: string
          receiver_id?: string
          sender_id?: string
          status?: string
        }
        Relationships: [
          {
            foreignKeyName: "message_requests_receiver_id_fkey"
            columns: ["receiver_id"]
            isOneToOne: false
            referencedRelation: "profiles"
            referencedColumns: ["id"]
          },
          {
            foreignKeyName: "message_requests_sender_id_fkey"
            columns: ["sender_id"]
            isOneToOne: false
            referencedRelation: "profiles"
            referencedColumns: ["id"]
          },
        ]
      }
      messages: {
        Row: {
          body: string
          created_at: string
          id: string
          read_at: string | null
          receiver_id: string
          sender_id: string
          thread_id: string
        }
        Insert: {
          body: string
          created_at?: string
          id?: string
          read_at?: string | null
          receiver_id: string
          sender_id: string
          thread_id: string
        }
        Update: {
          body?: string
          created_at?: string
          id?: string
          read_at?: string | null
          receiver_id?: string
          sender_id?: string
          thread_id?: string
        }
        Relationships: [
          {
            foreignKeyName: "messages_receiver_id_fkey"
            columns: ["receiver_id"]
            isOneToOne: false
            referencedRelation: "profiles"
            referencedColumns: ["id"]
          },
          {
            foreignKeyName: "messages_sender_id_fkey"
            columns: ["sender_id"]
            isOneToOne: false
            referencedRelation: "profiles"
            referencedColumns: ["id"]
          },
        ]
      }
      pings: {
        Row: {
          created_at: string
          expires_at: string
          label: string
          lat: number
          lon: number
          user_id: string
        }
        Insert: {
          created_at?: string
          expires_at: string
          label?: string
          lat: number
          lon: number
          user_id: string
        }
        Update: {
          created_at?: string
          expires_at?: string
          label?: string
          lat?: number
          lon?: number
          user_id?: string
        }
        Relationships: [
          {
            foreignKeyName: "pings_user_id_fkey"
            columns: ["user_id"]
            isOneToOne: true
            referencedRelation: "profiles"
            referencedColumns: ["id"]
          },
        ]
      }
      profiles: {
        Row: {
          avatar_emoji: string
          bio: string
          club_enabled: boolean
          coarse_lat: number | null
          coarse_lon: number | null
          created_at: string
          display_name: string
          home_region: string
          id: string
          is_pro: boolean
          last_status_change_at: string
          level: number
          scan_radius_miles: number
          status: string
          total_xp: number
        }
        Insert: {
          avatar_emoji?: string
          bio?: string
          club_enabled?: boolean
          coarse_lat?: number | null
          coarse_lon?: number | null
          created_at?: string
          display_name?: string
          home_region?: string
          id: string
          is_pro?: boolean
          last_status_change_at?: string
          level?: number
          scan_radius_miles?: number
          status?: string
          total_xp?: number
        }
        Update: {
          avatar_emoji?: string
          bio?: string
          club_enabled?: boolean
          coarse_lat?: number | null
          coarse_lon?: number | null
          created_at?: string
          display_name?: string
          home_region?: string
          id?: string
          is_pro?: boolean
          last_status_change_at?: string
          level?: number
          scan_radius_miles?: number
          status?: string
          total_xp?: number
        }
        Relationships: []
      }
      push_tokens: {
        Row: {
          created_at: string
          platform: string
          token: string
          user_id: string
        }
        Insert: {
          created_at?: string
          platform?: string
          token: string
          user_id: string
        }
        Update: {
          created_at?: string
          platform?: string
          token?: string
          user_id?: string
        }
        Relationships: [
          {
            foreignKeyName: "push_tokens_user_id_fkey"
            columns: ["user_id"]
            isOneToOne: false
            referencedRelation: "profiles"
            referencedColumns: ["id"]
          },
        ]
      }
      rockscout_collection: {
        Row: {
          created_at: string
          id: string
          notes: string
          specimen_id: string
          user_id: string
        }
        Insert: {
          created_at?: string
          id?: string
          notes?: string
          specimen_id: string
          user_id: string
        }
        Update: {
          created_at?: string
          id?: string
          notes?: string
          specimen_id?: string
          user_id?: string
        }
        Relationships: [
          {
            foreignKeyName: "rockscout_collection_specimen_id_fkey"
            columns: ["specimen_id"]
            isOneToOne: false
            referencedRelation: "specimen_catalog"
            referencedColumns: ["id"]
          },
        ]
      }
      rockscout_connections: {
        Row: {
          created_at: string
          id: string
          user_a: string
          user_b: string
        }
        Insert: {
          created_at?: string
          id?: string
          user_a: string
          user_b: string
        }
        Update: {
          created_at?: string
          id?: string
          user_a?: string
          user_b?: string
        }
        Relationships: []
      }
      rockscout_favorite_spots: {
        Row: {
          created_at: string
          id: string
          latitude: number
          longitude: number
          name: string
          region: string
          spot_id: string
          spot_type: string
          user_id: string
        }
        Insert: {
          created_at?: string
          id?: string
          latitude?: number
          longitude?: number
          name: string
          region?: string
          spot_id: string
          spot_type?: string
          user_id: string
        }
        Update: {
          created_at?: string
          id?: string
          latitude?: number
          longitude?: number
          name?: string
          region?: string
          spot_id?: string
          spot_type?: string
          user_id?: string
        }
        Relationships: []
      }
      rockscout_field_journal: {
        Row: {
          created_at: string
          dig_site_id: string | null
          entry_date: string
          id: string
          location: string
          notes: string
          photo_urls: Json
          updated_at: string
          user_id: string
          weather_summary: string
        }
        Insert: {
          created_at?: string
          dig_site_id?: string | null
          entry_date?: string
          id?: string
          location?: string
          notes?: string
          photo_urls?: Json
          updated_at?: string
          user_id: string
          weather_summary?: string
        }
        Update: {
          created_at?: string
          dig_site_id?: string | null
          entry_date?: string
          id?: string
          location?: string
          notes?: string
          photo_urls?: Json
          updated_at?: string
          user_id?: string
          weather_summary?: string
        }
        Relationships: []
      }
      rockscout_message_requests: {
        Row: {
          body: string
          created_at: string
          id: string
          listing_id: string | null
          recipient_id: string
          responded_at: string | null
          sender_id: string
          status: string
        }
        Insert: {
          body?: string
          created_at?: string
          id?: string
          listing_id?: string | null
          recipient_id: string
          responded_at?: string | null
          sender_id: string
          status?: string
        }
        Update: {
          body?: string
          created_at?: string
          id?: string
          listing_id?: string | null
          recipient_id?: string
          responded_at?: string | null
          sender_id?: string
          status?: string
        }
        Relationships: []
      }
      rockscout_messages: {
        Row: {
          body: string
          created_at: string
          id: string
          read_at: string | null
          sender_id: string
          thread_id: string
        }
        Insert: {
          body: string
          created_at?: string
          id?: string
          read_at?: string | null
          sender_id: string
          thread_id: string
        }
        Update: {
          body?: string
          created_at?: string
          id?: string
          read_at?: string | null
          sender_id?: string
          thread_id?: string
        }
        Relationships: [
          {
            foreignKeyName: "rockscout_messages_thread_id_fkey"
            columns: ["thread_id"]
            isOneToOne: false
            referencedRelation: "rockscout_threads"
            referencedColumns: ["id"]
          },
        ]
      }
      rockscout_pings: {
        Row: {
          created_at: string
          expires_at: string
          id: string
          label: string
          lat: number
          lng: number
          user_id: string
        }
        Insert: {
          created_at?: string
          expires_at: string
          id?: string
          label?: string
          lat: number
          lng: number
          user_id: string
        }
        Update: {
          created_at?: string
          expires_at?: string
          id?: string
          label?: string
          lat?: number
          lng?: number
          user_id?: string
        }
        Relationships: []
      }
      rockscout_profiles: {
        Row: {
          avatar_emoji: string
          club_enabled: boolean
          coarse_lat: number | null
          coarse_lng: number | null
          coarse_updated_at: string | null
          created_at: string
          display_name: string
          id: string
          is_pro: boolean
          last_sponsored_prompt_at: string | null
          last_status_change_at: string | null
          level: number
          pro_badge: boolean
          push_token: string | null
          push_token_updated_at: string | null
          referral_code: string | null
          referred_by: string | null
          scan_radius_miles: number
          status: string
          tokens: number
          unlock_until: string | null
          updated_at: string
          xp: number
        }
        Insert: {
          avatar_emoji?: string
          club_enabled?: boolean
          coarse_lat?: number | null
          coarse_lng?: number | null
          coarse_updated_at?: string | null
          created_at?: string
          display_name?: string
          id: string
          is_pro?: boolean
          last_sponsored_prompt_at?: string | null
          last_status_change_at?: string | null
          level?: number
          pro_badge?: boolean
          push_token?: string | null
          push_token_updated_at?: string | null
          referral_code?: string | null
          referred_by?: string | null
          scan_radius_miles?: number
          status?: string
          tokens?: number
          unlock_until?: string | null
          updated_at?: string
          xp?: number
        }
        Update: {
          avatar_emoji?: string
          club_enabled?: boolean
          coarse_lat?: number | null
          coarse_lng?: number | null
          coarse_updated_at?: string | null
          created_at?: string
          display_name?: string
          id?: string
          is_pro?: boolean
          last_sponsored_prompt_at?: string | null
          last_status_change_at?: string | null
          level?: number
          pro_badge?: boolean
          push_token?: string | null
          push_token_updated_at?: string | null
          referral_code?: string | null
          referred_by?: string | null
          scan_radius_miles?: number
          status?: string
          tokens?: number
          unlock_until?: string | null
          updated_at?: string
          xp?: number
        }
        Relationships: []
      }
      rockscout_push_subscriptions: {
        Row: {
          auth_key: string
          categories: Json
          created_at: string
          endpoint: string
          id: string
          p256dh_key: string
          platform: string
          updated_at: string
          user_id: string
        }
        Insert: {
          auth_key: string
          categories?: Json
          created_at?: string
          endpoint: string
          id?: string
          p256dh_key: string
          platform?: string
          updated_at?: string
          user_id: string
        }
        Update: {
          auth_key?: string
          categories?: Json
          created_at?: string
          endpoint?: string
          id?: string
          p256dh_key?: string
          platform?: string
          updated_at?: string
          user_id?: string
        }
        Relationships: []
      }
      rockscout_threads: {
        Row: {
          created_at: string
          id: string
          last_message_at: string
          user_a: string
          user_b: string
        }
        Insert: {
          created_at?: string
          id?: string
          last_message_at?: string
          user_a: string
          user_b: string
        }
        Update: {
          created_at?: string
          id?: string
          last_message_at?: string
          user_a?: string
          user_b?: string
        }
        Relationships: []
      }
      rockscout_trips: {
        Row: {
          completed_at: string | null
          created_at: string
          gear_checklist: Json
          id: string
          is_archived: boolean
          name: string
          notes: string
          stops: Json
          target_specimens: Json
          trip_date: string
          updated_at: string
          user_id: string
        }
        Insert: {
          completed_at?: string | null
          created_at?: string
          gear_checklist?: Json
          id?: string
          is_archived?: boolean
          name: string
          notes?: string
          stops?: Json
          target_specimens?: Json
          trip_date?: string
          updated_at?: string
          user_id: string
        }
        Update: {
          completed_at?: string | null
          created_at?: string
          gear_checklist?: Json
          id?: string
          is_archived?: boolean
          name?: string
          notes?: string
          stops?: Json
          target_specimens?: Json
          trip_date?: string
          updated_at?: string
          user_id?: string
        }
        Relationships: []
      }
      rockscout_wishlist: {
        Row: {
          created_at: string
          id: string
          specimen_id: string
          user_id: string
        }
        Insert: {
          created_at?: string
          id?: string
          specimen_id: string
          user_id: string
        }
        Update: {
          created_at?: string
          id?: string
          specimen_id?: string
          user_id?: string
        }
        Relationships: [
          {
            foreignKeyName: "rockscout_wishlist_specimen_id_fkey"
            columns: ["specimen_id"]
            isOneToOne: false
            referencedRelation: "specimen_catalog"
            referencedColumns: ["id"]
          },
        ]
      }
      specimen_catalog: {
        Row: {
          category: string
          colors: string
          created_at: string
          crystal_system: string
          description: string | null
          formation: string | null
          hardness: string
          id: string
          image_url: string
          luster: string
          name: string
          rarity: string
          streak: string
          tagline: string
          where_found: string | null
        }
        Insert: {
          category?: string
          colors?: string
          created_at?: string
          crystal_system?: string
          description?: string | null
          formation?: string | null
          hardness?: string
          id: string
          image_url?: string
          luster?: string
          name: string
          rarity?: string
          streak?: string
          tagline?: string
          where_found?: string | null
        }
        Update: {
          category?: string
          colors?: string
          created_at?: string
          crystal_system?: string
          description?: string | null
          formation?: string | null
          hardness?: string
          id?: string
          image_url?: string
          luster?: string
          name?: string
          rarity?: string
          streak?: string
          tagline?: string
          where_found?: string | null
        }
        Relationships: []
      }
      specimen_embeddings: {
        Row: {
          created_at: string
          embedding_model: string
          id: number
          image_url: string
          specimen_id: string
          text_embedding: string | null
        }
        Insert: {
          created_at?: string
          embedding_model?: string
          id?: never
          image_url: string
          specimen_id: string
          text_embedding?: string | null
        }
        Update: {
          created_at?: string
          embedding_model?: string
          id?: never
          image_url?: string
          specimen_id?: string
          text_embedding?: string | null
        }
        Relationships: []
      }
    }
    Views: {
      [_ in never]: never
    }
    Functions: {
      cleanup_expired_pings: { Args: never; Returns: undefined }
      match_artifact_embeddings: {
        Args: { match_count?: number; query_embedding: string }
        Returns: {
          artifact_id: string
          max_similarity: number
        }[]
      }
      match_specimen_embeddings: {
        Args: { match_count?: number; query_embedding: string }
        Returns: {
          max_similarity: number
          specimen_id: string
        }[]
      }
    }
    Enums: {
      [_ in never]: never
    }
    CompositeTypes: {
      [_ in never]: never
    }
  }
}

type DatabaseWithoutInternals = Omit<Database, "__InternalSupabase">

type DefaultSchema = DatabaseWithoutInternals[Extract<keyof Database, "public">]

export type Tables<
  DefaultSchemaTableNameOrOptions extends
    | keyof (DefaultSchema["Tables"] & DefaultSchema["Views"])
    | { schema: keyof DatabaseWithoutInternals },
  TableName extends DefaultSchemaTableNameOrOptions extends {
    schema: keyof DatabaseWithoutInternals
  }
    ? keyof (DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Tables"] &
        DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Views"])
    : never = never,
> = DefaultSchemaTableNameOrOptions extends {
  schema: keyof DatabaseWithoutInternals
}
  ? (DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Tables"] &
      DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Views"])[TableName] extends {
      Row: infer R
    }
    ? R
    : never
  : DefaultSchemaTableNameOrOptions extends keyof (DefaultSchema["Tables"] &
        DefaultSchema["Views"])
    ? (DefaultSchema["Tables"] &
        DefaultSchema["Views"])[DefaultSchemaTableNameOrOptions] extends {
        Row: infer R
      }
      ? R
      : never
    : never

export type TablesInsert<
  DefaultSchemaTableNameOrOptions extends
    | keyof DefaultSchema["Tables"]
    | { schema: keyof DatabaseWithoutInternals },
  TableName extends DefaultSchemaTableNameOrOptions extends {
    schema: keyof DatabaseWithoutInternals
  }
    ? keyof DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Tables"]
    : never = never,
> = DefaultSchemaTableNameOrOptions extends {
  schema: keyof DatabaseWithoutInternals
}
  ? DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Tables"][TableName] extends {
      Insert: infer I
    }
    ? I
    : never
  : DefaultSchemaTableNameOrOptions extends keyof DefaultSchema["Tables"]
    ? DefaultSchema["Tables"][DefaultSchemaTableNameOrOptions] extends {
        Insert: infer I
      }
      ? I
      : never
    : never

export type TablesUpdate<
  DefaultSchemaTableNameOrOptions extends
    | keyof DefaultSchema["Tables"]
    | { schema: keyof DatabaseWithoutInternals },
  TableName extends DefaultSchemaTableNameOrOptions extends {
    schema: keyof DatabaseWithoutInternals
  }
    ? keyof DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Tables"]
    : never = never,
> = DefaultSchemaTableNameOrOptions extends {
  schema: keyof DatabaseWithoutInternals
}
  ? DatabaseWithoutInternals[DefaultSchemaTableNameOrOptions["schema"]]["Tables"][TableName] extends {
      Update: infer U
    }
    ? U
    : never
  : DefaultSchemaTableNameOrOptions extends keyof DefaultSchema["Tables"]
    ? DefaultSchema["Tables"][DefaultSchemaTableNameOrOptions] extends {
        Update: infer U
      }
      ? U
      : never
    : never

export type Enums<
  DefaultSchemaEnumNameOrOptions extends
    | keyof DefaultSchema["Enums"]
    | { schema: keyof DatabaseWithoutInternals },
  EnumName extends DefaultSchemaEnumNameOrOptions extends {
    schema: keyof DatabaseWithoutInternals
  }
    ? keyof DatabaseWithoutInternals[DefaultSchemaEnumNameOrOptions["schema"]]["Enums"]
    : never = never,
> = DefaultSchemaEnumNameOrOptions extends {
  schema: keyof DatabaseWithoutInternals
}
  ? DatabaseWithoutInternals[DefaultSchemaEnumNameOrOptions["schema"]]["Enums"][EnumName]
  : DefaultSchemaEnumNameOrOptions extends keyof DefaultSchema["Enums"]
    ? DefaultSchema["Enums"][DefaultSchemaEnumNameOrOptions]
    : never

export type CompositeTypes<
  PublicCompositeTypeNameOrOptions extends
    | keyof DefaultSchema["CompositeTypes"]
    | { schema: keyof DatabaseWithoutInternals },
  CompositeTypeName extends PublicCompositeTypeNameOrOptions extends {
    schema: keyof DatabaseWithoutInternals
  }
    ? keyof DatabaseWithoutInternals[PublicCompositeTypeNameOrOptions["schema"]]["CompositeTypes"]
    : never = never,
> = PublicCompositeTypeNameOrOptions extends {
  schema: keyof DatabaseWithoutInternals
}
  ? DatabaseWithoutInternals[PublicCompositeTypeNameOrOptions["schema"]]["CompositeTypes"][CompositeTypeName]
  : PublicCompositeTypeNameOrOptions extends keyof DefaultSchema["CompositeTypes"]
    ? DefaultSchema["CompositeTypes"][PublicCompositeTypeNameOrOptions]
    : never

export const Constants = {
  public: {
    Enums: {},
  },
} as const
