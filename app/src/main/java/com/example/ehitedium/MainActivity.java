package com.example.ehitedium;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Gravity;
import android.view.View;
import android.view.ViewOutlineProvider;
import android.widget.Button;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Locale;

public class MainActivity extends Activity {
    private DummyRenderView renderView;
    private EditText hexInput;
    private TextView alphaValue;
    private SeekBar alphaSeek;
    private LinearLayout controlPanel;
    private int selectedColor = Color.rgb(0, 255, 120);

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        FrameLayout root = new FrameLayout(this);
        renderView = new DummyRenderView(this);
        root.addView(renderView, new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT));

        ScrollView panelScroll = new ScrollView(this);
        panelScroll.setFillViewport(false);
        panelScroll.setBackground(roundRect(Color.argb(230, 20, 26, 36), 18));
        FrameLayout.LayoutParams panelParams = new FrameLayout.LayoutParams(
                dp(300), FrameLayout.LayoutParams.MATCH_PARENT, Gravity.TOP | Gravity.START);
        panelParams.setMargins(dp(12), dp(12), dp(12), dp(12));
        root.addView(panelScroll, panelParams);

        controlPanel = new LinearLayout(this);
        controlPanel.setOrientation(LinearLayout.VERTICAL);
        controlPanel.setPadding(dp(16), dp(16), dp(16), dp(16));
        panelScroll.addView(controlPanel);

        TextView title = text("Ehitedium • Visual Demo", 20, Color.WHITE);
        controlPanel.addView(title);
        TextView subtitle = text("Standalone test mannequins — no game integration", 12, Color.LTGRAY);
        subtitle.setPadding(0, dp(4), 0, dp(14));
        controlPanel.addView(subtitle);

        addToggle("Показывать контуры", true, new ToggleAction() {
            @Override public void set(boolean value) { renderView.setShowBox(value); }
        });
        addToggle("Цветная заливка", true, new ToggleAction() {
            @Override public void set(boolean value) { renderView.setShowChams(value); }
        });
        addToggle("Подписи моделей", true, new ToggleAction() {
            @Override public void set(boolean value) { renderView.setShowText(value); }
        });
        addToggle("Ночной фон", false, new ToggleAction() {
            @Override public void set(boolean value) { renderView.setNightMode(value); }
        });

        TextView colorLabel = text("HEX-цвет (#RRGGBB)", 14, Color.WHITE);
        colorLabel.setPadding(0, dp(14), 0, dp(4));
        controlPanel.addView(colorLabel);

        hexInput = new EditText(this);
        hexInput.setSingleLine(true);
        hexInput.setText("#00FF78");
        hexInput.setTextColor(Color.WHITE);
        hexInput.setHintTextColor(Color.LTGRAY);
        hexInput.setHint("#00FF78");
        hexInput.setPadding(dp(10), dp(4), dp(10), dp(4));
        hexInput.setBackground(roundRect(Color.argb(80, 255, 255, 255), 8));
        controlPanel.addView(hexInput, new LinearLayout.LayoutParams(
                LinearLayout.LayoutParams.MATCH_PARENT, dp(48)));

        hexInput.addTextChangedListener(new TextWatcher() {
            @Override public void beforeTextChanged(CharSequence s, int start, int count, int after) {}
            @Override public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (ColorUtils.isValidHex(s.toString())) {
                    selectedColor = ColorUtils.parseHex(s.toString());
                    renderView.setChamsColor(selectedColor);
                }
            }
            @Override public void afterTextChanged(Editable s) {}
        });

        TextView alphaLabel = text("Прозрачность заливки", 14, Color.WHITE);
        alphaLabel.setPadding(0, dp(12), 0, 0);
        controlPanel.addView(alphaLabel);
        alphaValue = text("145 / 255", 12, Color.LTGRAY);
        controlPanel.addView(alphaValue);
        alphaSeek = new SeekBar(this);
        alphaSeek.setMax(255);
        alphaSeek.setProgress(145);
        controlPanel.addView(alphaSeek);
        alphaSeek.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                renderView.setChamsAlpha(progress);
                alphaValue.setText(progress + " / 255");
            }
            @Override public void onStartTrackingTouch(SeekBar seekBar) {}
            @Override public void onStopTrackingTouch(SeekBar seekBar) {}
        });

        TextView paletteLabel = text("Быстрые цвета", 14, Color.WHITE);
        paletteLabel.setPadding(0, dp(10), 0, dp(4));
        controlPanel.addView(paletteLabel);
        LinearLayout palette = new LinearLayout(this);
        palette.setGravity(Gravity.CENTER_VERTICAL);
        int[] colors = new int[]{Color.rgb(0,255,120), Color.CYAN, Color.MAGENTA, Color.YELLOW, Color.RED};
        for (final int color : colors) {
            View swatch = new View(this);
            GradientDrawable bg = roundRect(color, 50);
            swatch.setBackground(bg);
            LinearLayout.LayoutParams p = new LinearLayout.LayoutParams(dp(34), dp(34));
            p.setMargins(0, 0, dp(8), 0);
            palette.addView(swatch, p);
            swatch.setOnClickListener(new View.OnClickListener() {
                @Override public void onClick(View v) {
                    selectedColor = color;
                    renderView.setChamsColor(color);
                    hexInput.setText(ColorUtils.toHex(color));
                }
            });
        }
        controlPanel.addView(palette);

        LinearLayout buttons = new LinearLayout(this);
        buttons.setOrientation(LinearLayout.HORIZONTAL);
        buttons.setPadding(0, dp(14), 0, 0);
        Button save = new Button(this);
        save.setText("Сохранить");
        Button load = new Button(this);
        load.setText("Загрузить");
        buttons.addView(save, new LinearLayout.LayoutParams(0, dp(48), 1));
        LinearLayout.LayoutParams loadParams = new LinearLayout.LayoutParams(0, dp(48), 1);
        loadParams.setMargins(dp(8), 0, 0, 0);
        buttons.addView(load, loadParams);
        controlPanel.addView(buttons);
        save.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Toast.makeText(MainActivity.this,
                        saveConfig() ? "Настройки сохранены" : "Не удалось сохранить настройки",
                        Toast.LENGTH_SHORT).show();
            }
        });
        load.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                Toast.makeText(MainActivity.this,
                        loadConfig() ? "Настройки загружены" : "Конфигурация не найдена или повреждена",
                        Toast.LENGTH_SHORT).show();
            }
        });

        FrameLayout catButton = new FrameLayout(this);
        catButton.setBackground(circleBorder());
        catButton.setContentDescription("Свернуть или открыть меню");
        catButton.setElevation(dp(8));
        FrameLayout.LayoutParams catParams = new FrameLayout.LayoutParams(dp(60), dp(60),
                Gravity.BOTTOM | Gravity.END);
        catParams.setMargins(dp(24), dp(24), dp(24), dp(24));
        root.addView(catButton, catParams);

        // Built-in vector-like cat drawing, so the project compiles without an external image file.
        CatIconView cat = new CatIconView(this);
        FrameLayout.LayoutParams catImageParams = new FrameLayout.LayoutParams(
                FrameLayout.LayoutParams.MATCH_PARENT, FrameLayout.LayoutParams.MATCH_PARENT);
        catImageParams.setMargins(dp(4), dp(4), dp(4), dp(4));
        catButton.addView(cat, catImageParams);
        catButton.setOnClickListener(new View.OnClickListener() {
            @Override public void onClick(View v) {
                if (controlPanel.getVisibility() == View.VISIBLE) {
                    controlPanel.setVisibility(View.GONE);
                    Toast.makeText(MainActivity.this, "Меню скрыто", Toast.LENGTH_SHORT).show();
                } else {
                    controlPanel.setVisibility(View.VISIBLE);
                    Toast.makeText(MainActivity.this, "Меню открыто", Toast.LENGTH_SHORT).show();
                }
            }
        });

        setContentView(root);
    }

    private void addToggle(String label, boolean initial, final ToggleAction action) {
        final android.widget.Switch toggle = new android.widget.Switch(this);
        toggle.setText(label);
        toggle.setTextColor(Color.WHITE);
        toggle.setChecked(initial);
        toggle.setPadding(0, dp(5), 0, dp(5));
        toggle.setOnCheckedChangeListener((buttonView, isChecked) -> action.set(isChecked));
        controlPanel.addView(toggle);
    }

    private boolean saveConfig() {
        try {
            JSONObject json = new JSONObject();
            json.put("color", ColorUtils.toHex(selectedColor));
            json.put("alpha", renderView.getChamsAlpha());
            json.put("showBox", renderView.isShowBox());
            json.put("showChams", renderView.isShowChams());
            json.put("showText", renderView.isShowText());
            json.put("nightMode", renderView.isNightMode());
            File dir = new File(getFilesDir(), "Ehitedium");
            if (!dir.exists() && !dir.mkdirs()) return false;
            File file = new File(dir, "config.json");
            FileOutputStream out = new FileOutputStream(file);
            try {
                out.write(json.toString(2).getBytes(StandardCharsets.UTF_8));
                out.flush();
            } finally {
                out.close();
            }
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private boolean loadConfig() {
        File file = new File(new File(getFilesDir(), "Ehitedium"), "config.json");
        if (!file.isFile() || file.length() > 16384) return false;
        try {
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(
                    new FileInputStream(file), StandardCharsets.UTF_8));
            try {
                String line;
                while ((line = reader.readLine()) != null) builder.append(line);
            } finally {
                reader.close();
            }
            JSONObject json = new JSONObject(builder.toString());
            String color = json.optString("color", "#00FF78");
            if (!ColorUtils.isValidHex(color)) return false;
            int alpha = json.optInt("alpha", 145);
            if (alpha < 0 || alpha > 255) return false;
            selectedColor = ColorUtils.parseHex(color);
            renderView.setChamsColor(selectedColor);
            renderView.setChamsAlpha(alpha);
            alphaSeek.setProgress(alpha);
            hexInput.setText(ColorUtils.toHex(selectedColor));
            renderView.setShowBox(json.optBoolean("showBox", true));
            renderView.setShowChams(json.optBoolean("showChams", true));
            renderView.setShowText(json.optBoolean("showText", true));
            renderView.setNightMode(json.optBoolean("nightMode", false));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    private TextView text(String value, float size, int color) {
        TextView view = new TextView(this);
        view.setText(value);
        view.setTextSize(size);
        view.setTextColor(color);
        return view;
    }

    private GradientDrawable roundRect(int color, int radiusDp) {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setColor(color);
        drawable.setCornerRadius(dp(radiusDp));
        return drawable;
    }

    private GradientDrawable circleBorder() {
        GradientDrawable drawable = new GradientDrawable();
        drawable.setShape(GradientDrawable.OVAL);
        drawable.setColor(Color.TRANSPARENT);
        drawable.setStroke(dp(3), Color.rgb(0, 255, 120));
        return drawable;
    }

    private int dp(int value) {
        return Math.round(value * getResources().getDisplayMetrics().density);
    }

    private interface ToggleAction {
        void set(boolean value);
    }

    private static class CatIconView extends View {
        private final android.graphics.Paint p = new android.graphics.Paint(android.graphics.Paint.ANTI_ALIAS_FLAG);
        CatIconView(Context context) { super(context); }
        @Override protected void onDraw(android.graphics.Canvas canvas) {
            super.onDraw(canvas);
            float w = getWidth(), h = getHeight();
            p.setColor(Color.rgb(225, 235, 240));
            p.setStyle(android.graphics.Paint.Style.FILL);
            android.graphics.Path head = new android.graphics.Path();
            head.moveTo(w * .18f, h * .36f);
            head.lineTo(w * .14f, h * .08f);
            head.lineTo(w * .38f, h * .22f);
            head.quadTo(w * .5f, h * .16f, w * .62f, h * .22f);
            head.lineTo(w * .86f, h * .08f);
            head.lineTo(w * .82f, h * .36f);
            head.quadTo(w * .94f, h * .76f, w * .5f, h * .82f);
            head.quadTo(w * .06f, h * .76f, w * .18f, h * .36f);
            canvas.drawPath(head, p);
            p.setColor(Color.rgb(35, 45, 55));
            canvas.drawCircle(w * .37f, h * .48f, w * .035f, p);
            canvas.drawCircle(w * .63f, h * .48f, w * .035f, p);
            p.setColor(Color.rgb(0, 180, 95));
            canvas.drawCircle(w * .5f, h * .59f, w * .035f, p);
            p.setStyle(android.graphics.Paint.Style.STROKE);
            p.setStrokeWidth(Math.max(1f, w * .018f));
            p.setColor(Color.rgb(35, 45, 55));
            canvas.drawArc(w * .42f, h * .58f, w * .5f, h * .68f, 0, 90, false, p);
            canvas.drawArc(w * .5f, h * .58f, w * .58f, h * .68f, 0, 90, false, p);
        }
    }
}
