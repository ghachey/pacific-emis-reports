package com.rapidservices.reporting.charts;

import net.sf.jasperreports.engine.JRChart;
import net.sf.jasperreports.engine.JRChartCustomizer;
import net.sf.jasperreports.engine.JRPropertiesMap;

import org.jfree.chart.JFreeChart;
import org.jfree.chart.axis.*;
import org.jfree.chart.plot.CategoryPlot;
import org.jfree.chart.plot.PlotRenderingInfo;
import org.jfree.chart.renderer.category.GroupedStackedBarRenderer;
import org.jfree.chart.util.ParamChecks;
import org.jfree.data.KeyToGroupMap;
import org.jfree.data.category.DefaultCategoryDataset;
import org.jfree.text.TextBlock;
import org.jfree.text.TextBlockAnchor;
import org.jfree.text.TextUtilities;
import org.jfree.ui.HorizontalAlignment;
import org.jfree.ui.RectangleAnchor;
import org.jfree.ui.RectangleEdge;
import org.jfree.ui.TextAnchor;

import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics2D;
import java.awt.geom.Rectangle2D;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

import static org.jfree.text.TextBlockAnchor.*;

public class StackBarChartCustomizer implements JRChartCustomizer {

    private static final int DEFAULT_LINES_TO_SHOW = 2;
    private static final String LINES_TO_SHOW_PROPERTY = "lines.to.show";
    private static final String CATEGORY_MARGIN_PROPERTY = "category.margin";
    private static final String ITEM_MARGIN_PROPERTY = "item.margin";
    private static final String LABEL_POSITION_OFFSET_PROPERTY = "label.position.offset";
    private static final String CATEGORY_WIDTH_RATIO_PROPERTY = "category.width.ratio";
    private static final String CATEGORY_FONT_NAME_PROPERTY = "category.font.name";
    private static final String CATEGORY_FONT_SIZE_PROPERTY = "category.font.size";
    private static final double DEFAULT_CATEGORY_MARGIN = 0.05;
    private static final double DEFAULT_ITEM_MARGIN = 0.05;
    private static final int DEFAULT_LABEL_POSITION_OFFSET = 100;
    private static final float DEFAULT_CATEGORY_WIDTH_RATIO = 0.4f;

    @SuppressWarnings("MethodWithMoreThanThreeNegations")
    @Override
    public void customize(JFreeChart chart, JRChart jasperChart) {
        JRPropertiesMap map = jasperChart.getPropertiesMap();
        int linesToShow = DEFAULT_LINES_TO_SHOW;
        double categoryMargin = DEFAULT_CATEGORY_MARGIN;
        double itemMargin = DEFAULT_ITEM_MARGIN;
        int labelPositionOffset = DEFAULT_LABEL_POSITION_OFFSET;
        float categoryWidthRatio = DEFAULT_CATEGORY_WIDTH_RATIO;
        String categoryFontName = null;
        Integer categoryFontSize = null;

        if (!isNullOrEmpty(map.getProperty(LINES_TO_SHOW_PROPERTY))) {
            try {
                linesToShow = Integer.valueOf(map.getProperty(LINES_TO_SHOW_PROPERTY));
            } catch (NumberFormatException ignored) {
                // ignore
            }
        }
        if (!isNullOrEmpty(map.getProperty(CATEGORY_MARGIN_PROPERTY))) {
            try {
                categoryMargin = Double.valueOf(map.getProperty(CATEGORY_MARGIN_PROPERTY));
            } catch (NumberFormatException ignored) {
                // ignore
            }
        }
        if (!isNullOrEmpty(map.getProperty(ITEM_MARGIN_PROPERTY))) {
            try {
                itemMargin = Double.valueOf(map.getProperty(ITEM_MARGIN_PROPERTY));
            } catch (NumberFormatException ignored) {
                // ignore
            }
        }
        if (!isNullOrEmpty(map.getProperty(LABEL_POSITION_OFFSET_PROPERTY))) {
            try {
                labelPositionOffset = Integer.valueOf(map.getProperty(LABEL_POSITION_OFFSET_PROPERTY));
            } catch (NumberFormatException ignored) {
                // ignore
            }
        }
        if (!isNullOrEmpty(map.getProperty(CATEGORY_WIDTH_RATIO_PROPERTY))) {
            try {
                categoryWidthRatio = Float.valueOf(map.getProperty(CATEGORY_WIDTH_RATIO_PROPERTY));
            } catch (NumberFormatException ignored) {
                // ignore
            }
        }
        if (!isNullOrEmpty(map.getProperty(CATEGORY_FONT_NAME_PROPERTY))) {
            try {
                categoryFontName = map.getProperty(CATEGORY_FONT_NAME_PROPERTY);
            } catch (NumberFormatException ignored) {
                // ignore
            }
        }
        if (!isNullOrEmpty(map.getProperty(CATEGORY_FONT_SIZE_PROPERTY))) {
            try {
                categoryFontSize = Integer.valueOf(map.getProperty(CATEGORY_FONT_SIZE_PROPERTY));
            } catch (NumberFormatException ignored) {
                // ignore
            }
        }

        CategoryPlot plot = chart.getCategoryPlot();

        DefaultCategoryDataset dataset = (DefaultCategoryDataset) plot.getDataset();

        List<String> columnsKeys = dataset.getColumnKeys();
        Set<String> keys = new LinkedHashSet();

        for (String columnKey : columnsKeys) {
            keys.add(columnKey.split("\\^")[0]);
        }

        CustomDataset customDataset = new CustomDataset(dataset);

        plot.setDataset(customDataset);

        GroupedStackedBarRenderer renderer = new GroupedStackedBarRenderer();

        KeyToGroupMap keyToGroupMap = new KeyToGroupMap("G1");

        int index = 0;
        for (String key : keys) {
            index++;
            keyToGroupMap.mapKeyToGroup(key, "G" + index);
        }

        renderer.setSeriesToGroupMap(keyToGroupMap);
        renderer.setItemMargin(itemMargin);

        plot.setRenderer(renderer);

        SubCategoryAxis subCategoryAxis = new CustomSubCategoryAxis("");
        subCategoryAxis.addSubCategory("F");

        plot.setDomainAxis(subCategoryAxis);
        CategoryAxis axis = plot.getDomainAxis();

        final CategoryLabelPosition left = new CategoryLabelPosition(
                RectangleAnchor.LEFT, TextBlockAnchor.CENTER_LEFT,
                TextAnchor.CENTER_LEFT, 0.0,
                CategoryLabelWidthType.RANGE, categoryWidthRatio
        );

        axis.setCategoryLabelPositions(CategoryLabelPositions
                .replaceLeftPosition(axis.getCategoryLabelPositions(), left));

        Font tickFont = axis.getTickLabelFont();
        if (categoryFontName == null) {
            categoryFontName = tickFont.getFontName();
        }

        if (categoryFontSize == null) {
            categoryFontSize = tickFont.getSize();
        }

        axis.setTickLabelFont(new Font(categoryFontName, tickFont.getStyle(), categoryFontSize));
        axis.setMaximumCategoryLabelLines(linesToShow);
        axis.setCategoryMargin(categoryMargin);
        axis.setCategoryLabelPositionOffset(labelPositionOffset);
    }

    @SuppressWarnings("QuestionableName")
    private static boolean isNullOrEmpty(String string) {
        return string == null || string.isEmpty();
    }

    class CustomSubCategoryAxis extends SubCategoryAxis {

        public CustomSubCategoryAxis(String label) {
            super(label);
        }

        @Override
        protected TextBlock createLabel(Comparable category, float width, RectangleEdge edge, Graphics2D g2) {
            TextBlock textBlock = super.createLabel(category, width, edge, g2);
            textBlock.setLineAlignment(HorizontalAlignment.LEFT);
            return textBlock;
        }

        @Override
        public List refreshTicks(Graphics2D g2, AxisState state, Rectangle2D dataArea, RectangleEdge edge) {
            List ticks = new java.util.ArrayList();

            // sanity check for data area...
            if (dataArea.getHeight() <= 0.0 || dataArea.getWidth() < 0.0) {
                return ticks;
            }

            CategoryPlot plot = (CategoryPlot) getPlot();
            List categories = plot.getCategoriesForAxis(this);
            double max = 0.0;

            if (categories != null) {
                CategoryLabelPosition position
                        = this.getCategoryLabelPositions().getLabelPosition(edge);
                float r = this.getMaximumCategoryLabelWidthRatio();
                if (r <= 0.0) {
                    r = position.getWidthRatio();
                }

                float l;
                if (position.getWidthType() == CategoryLabelWidthType.CATEGORY) {
                    l = (float) calculateCategorySize(categories.size(), dataArea,
                            edge);
                } else {
                    if (RectangleEdge.isLeftOrRight(edge)) {
                        l = (float) dataArea.getWidth();
                    } else {
                        l = (float) dataArea.getHeight();
                    }
                }
                int categoryIndex = 0;
                Iterator iterator = categories.iterator();
                while (iterator.hasNext()) {
                    Comparable category = (Comparable) iterator.next();
                    g2.setFont(getTickLabelFont(category));
                    TextBlock label = createLabel(category, l * r, edge, g2);
                    if (edge == RectangleEdge.TOP || edge == RectangleEdge.BOTTOM) {
                        max = Math.max(max, calculateTextBlockHeight(label,
                                position, g2));
                    } else if (edge == RectangleEdge.LEFT
                            || edge == RectangleEdge.RIGHT) {
                        max = Math.max(max, calculateTextBlockWidth(label,
                                position, g2));
                    }
                    Tick tick = new CategoryTick(category, label,
                            CENTER_RIGHT,
                            position.getRotationAnchor(), position.getAngle());
                    ticks.add(tick);
                    categoryIndex = categoryIndex + 1;
                }
            }
            state.setMax(max);
            return ticks;
        }

        @Override
        protected AxisState drawSubCategoryLabels(Graphics2D g2, Rectangle2D plotArea, Rectangle2D dataArea,
                                                  RectangleEdge edge, AxisState state, PlotRenderingInfo plotState) {
            ParamChecks.nullNotPermitted(state, "state");

            g2.setFont(this.getSubLabelFont());
            g2.setPaint(this.getSubLabelPaint());
            CategoryPlot plot = (CategoryPlot) getPlot();
            int categoryCount = 0;
            CustomDataset dataset = (CustomDataset) plot.getDataset();
            if (dataset != null) {
                categoryCount = dataset.getColumnCount();
            }

            double maxdim = getMaxDim(g2, edge);
            for (int categoryIndex = 0; categoryIndex < categoryCount;
                 categoryIndex++) {

                double x0 = 0.0;
                double x1 = 0.0;
                double y0 = 0.0;
                double y1 = 0.0;
                if (edge == RectangleEdge.TOP) {
                    x0 = getCategoryStart(categoryIndex, categoryCount, dataArea,
                            edge);
                    x1 = getCategoryEnd(categoryIndex, categoryCount, dataArea,
                            edge);
                    y1 = state.getCursor();
                    y0 = y1 - maxdim;
                } else if (edge == RectangleEdge.BOTTOM) {
                    x0 = getCategoryStart(categoryIndex, categoryCount, dataArea,
                            edge);
                    x1 = getCategoryEnd(categoryIndex, categoryCount, dataArea,
                            edge);
                    y0 = state.getCursor();
                    y1 = y0 + maxdim;
                } else if (edge == RectangleEdge.LEFT) {
                    y0 = getCategoryStart(categoryIndex, categoryCount, dataArea,
                            edge);
                    y1 = getCategoryEnd(categoryIndex, categoryCount, dataArea,
                            edge);
                    x1 = state.getCursor();
                    x0 = x1 - maxdim;
                } else if (edge == RectangleEdge.RIGHT) {
                    y0 = getCategoryStart(categoryIndex, categoryCount, dataArea,
                            edge);
                    y1 = getCategoryEnd(categoryIndex, categoryCount, dataArea,
                            edge);
                    x0 = state.getCursor();
                    x1 = x0 + maxdim;
                }
                Rectangle2D area = new Rectangle2D.Double(x0, y0, (x1 - x0),
                        (y1 - y0));
                int subCategoryCount = getSubCategories().size();
                float width = (float) ((x1 - x0) / subCategoryCount);
                float height = (float) ((y1 - y0) / subCategoryCount);
                float xx, yy;
                for (int i = 0; i < subCategoryCount; i++) {
                    if (RectangleEdge.isTopOrBottom(edge)) {
                        xx = (float) (x0 + (i + 0.5) * width);
                        yy = (float) area.getCenterY();
                    } else {
                        xx = (float) area.getCenterX();
//                        yy = (float) (y0 + (i + 0.5) * height);
                        yy = (float) area.getMinY();
                    }
                    String label = (String) dataset.getGender(categoryIndex);
                    TextUtilities.drawRotatedString(label, g2, xx - 10, yy,
                            TextAnchor.TOP_CENTER, 0.0, TextAnchor.CENTER);
                }
            }

            if (edge.equals(RectangleEdge.TOP)) {
                double h = maxdim;
                state.cursorUp(h);
            } else if (edge.equals(RectangleEdge.BOTTOM)) {
                double h = maxdim;
                state.cursorDown(h);
            } else if (edge == RectangleEdge.LEFT) {
                double w = maxdim;
                state.cursorLeft(w);
            } else if (edge == RectangleEdge.RIGHT) {
                double w = maxdim;
                state.cursorRight(w);
            }
            return state;
        }

        private double getMaxDim(Graphics2D g2, RectangleEdge edge) {
            double result = 0.0;
            g2.setFont(this.getSubLabelFont());
            FontMetrics fm = g2.getFontMetrics();
            Iterator iterator = getSubCategories().iterator();
            while (iterator.hasNext()) {
                Comparable subcategory = (Comparable) iterator.next();
                String label = subcategory.toString();
                Rectangle2D bounds = TextUtilities.getTextBounds(label, g2, fm);
                double dim;
                if (RectangleEdge.isLeftOrRight(edge)) {
                    dim = bounds.getWidth();
                } else {  // must be top or bottom
                    dim = bounds.getHeight();
                }
                result = Math.max(result, dim);
            }
            return result;
        }

        private List getSubCategories() {
            Field[] fs = this.getClass().getSuperclass().getDeclaredFields();
            for (Field field : fs) {
                if (field.getName().equals("subCategories")) {
                    field.setAccessible(true);

                    try {
                        return (List) field.get(this);
                    } catch (IllegalAccessException e) {
                        // ignored
                    }
                }
            }
            return new ArrayList();
        }
    }

    class CustomDataset extends DefaultCategoryDataset {
        private DefaultCategoryDataset dataset;

        public CustomDataset(DefaultCategoryDataset dataset) {
            this.dataset = dataset;
        }

        @Override
        public int getRowCount() {
            return dataset.getRowCount();
        }

        @Override
        public int getColumnCount() {
            return dataset.getColumnCount();
        }

        @Override
        public Number getValue(int row, int column) {
            return dataset.getValue(row, column);
        }

        @Override
        public Comparable getRowKey(int row) {
            return dataset.getRowKey(row);
        }

        @Override
        public int getRowIndex(Comparable key) {
            return dataset.getRowIndex(key);
        }

        @Override
        public List getRowKeys() {
            return dataset.getRowKeys();
        }

        @Override
        public Comparable getColumnKey(int column) {
            String columnKey = (String) dataset.getColumnKey(column);
            return columnKey.split("\\^")[0];
        }

        public Comparable getGender(int column) {
            String columnKey = (String) dataset.getColumnKey(column);
            return columnKey.split("\\^")[1];
        }

        @Override
        public int getColumnIndex(Comparable key) {
            return dataset.getColumnIndex(key);
        }

        @Override
        public List getColumnKeys() {
            return dataset.getColumnKeys();
        }

        @Override
        public Number getValue(Comparable rowKey, Comparable columnKey) {
            return dataset.getValue(rowKey, columnKey);
        }

        @Override
        public void addValue(Number value, Comparable rowKey, Comparable columnKey) {
            dataset.addValue(value, rowKey, columnKey);
        }

        @Override
        public void addValue(double value, Comparable rowKey, Comparable columnKey) {
            dataset.addValue(value, rowKey, columnKey);
        }

        @Override
        public void setValue(Number value, Comparable rowKey, Comparable columnKey) {
            dataset.setValue(value, rowKey, columnKey);
        }

        @Override
        public void setValue(double value, Comparable rowKey, Comparable columnKey) {
            dataset.setValue(value, rowKey, columnKey);
        }

        @Override
        public void incrementValue(double value, Comparable rowKey, Comparable columnKey) {
            dataset.incrementValue(value, rowKey, columnKey);
        }

        @Override
        public void removeValue(Comparable rowKey, Comparable columnKey) {
            dataset.removeValue(rowKey, columnKey);
        }

        @Override
        public void removeRow(int rowIndex) {
            dataset.removeRow(rowIndex);
        }

        @Override
        public void removeRow(Comparable rowKey) {
            dataset.removeRow(rowKey);
        }

        @Override
        public void removeColumn(int columnIndex) {
            dataset.removeColumn(columnIndex);
        }

        @Override
        public void removeColumn(Comparable columnKey) {
            dataset.removeColumn(columnKey);
        }
    }
}