package net.muxi.huashiapp.ui.location.overlay;

import android.content.Context;

import com.amap.api.maps.AMap;
import com.amap.api.maps.model.BitmapDescriptor;
import com.amap.api.maps.model.LatLng;
import com.amap.api.maps.model.MarkerOptions;
import com.amap.api.maps.model.PolylineOptions;
import com.amap.api.services.core.LatLonPoint;
import com.amap.api.services.route.WalkPath;
import com.amap.api.services.route.WalkStep;

import net.muxi.huashiapp.ui.location.AMapUtil;

import java.util.List;

/**
 * 步行路线图层类。在高德地图API里，如果要显示步行路线规划，可以用此类来创建步行路线图层。如不满足需求，也可以自己创建自定义的步行路线图层。
 * @since V2.1.0
 */
public class WalkRouteOverlay extends RouteOverlay {

    private PolylineOptions mPolylineOptions;

    private BitmapDescriptor walkStationDescriptor= null;

    private float time=0;
    private float distance=0;
    private WalkPath walkPath;
    public float getTime(){
        return time;
    }
    public float getDistance(){
        return distance;
    }
    /**
     * 通过此构造函数创建步行路线图层。
     * @param context 当前activity。
     * @param amap 地图对象。
     * @param path 步行路线规划的一个方案。详见搜索服务模块的路径查询包（com.amap.api.services.route）中的类
     * @param start 起点。详见搜索服务模块的核心基础包（com.amap.api.services.core）中的类
     * @param end 终点。详见搜索服务模块的核心基础包（com.amap.api.services.core）中的类
     * @since V2.1.0
     */
    public WalkRouteOverlay(Context context, AMap amap, WalkPath path,
                            LatLonPoint start, LatLonPoint end) {
        super(context);
        this.mAMap = amap;
        this.walkPath = path;
        mStartPoint = AMapUtil.convertToLatLng(start);
        mEndPoint = AMapUtil.convertToLatLng(end);
    }
    /**
     * 添加步行路线到地图中。
     * @since V2.1.0
     */
    public void addToMap() {

        initPolylineOptions();
        try {
            List<WalkStep> walkPaths = walkPath.getSteps();
            mPolylineOptions.add(mStartPoint);
            for (int i = 0; i < walkPaths.size(); i++) {
                WalkStep walkStep = walkPaths.get(i);
                LatLng latLng = AMapUtil.convertToLatLng(walkStep
                        .getPolyline().get(0));

                addWalkStationMarkers(walkStep, latLng);
                addWalkPolyLines(walkStep);

            }
            mPolylineOptions.add(mEndPoint);

            showPolyline();
        } catch (Throwable e) {
            e.printStackTrace();
        }
    }

    /**
     * 检查这一步的最后一点和下一步的起始点之间是否存在空隙
     */
    private void checkDistanceToNextStep(WalkStep walkStep,
                                         WalkStep walkStep1) {
        LatLonPoint lastPoint = getLastWalkPoint(walkStep);
        LatLonPoint nextFirstPoint = getFirstWalkPoint(walkStep1);
        if (!(lastPoint.equals(nextFirstPoint))) {
            addWalkPolyLine(lastPoint, nextFirstPoint);
        }
    }


    public LatLonPoint getLastWalkPoint(WalkStep walkStep) {
        return walkStep.getPolyline().get(walkStep.getPolyline().size() - 1);
    }

    private LatLonPoint getFirstWalkPoint(WalkStep walkStep) {
        return walkStep.getPolyline().get(0);
    }


    private void addWalkPolyLine(LatLonPoint pointFrom, LatLonPoint pointTo) {
        addWalkPolyLine(AMapUtil.convertToLatLng(pointFrom), AMapUtil.convertToLatLng(pointTo));
    }

    private void addWalkPolyLine(LatLng latLngFrom, LatLng latLngTo) {
        mPolylineOptions.add(latLngFrom, latLngTo);
    }


    private void addWalkPolyLines(WalkStep walkStep) {
        mPolylineOptions.addAll(AMapUtil.convertArrList(walkStep.getPolyline()));
    }


    private void addWalkStationMarkers(WalkStep walkStep, LatLng position) {
        addStationMarker(new MarkerOptions()
                .position(position)
                .infoWindowEnable(false)
                //方向，道路
                .title("道路: "+ walkStep.getRoad())
                .snippet("方向: " +walkStep.getInstruction())
                .visible(mNodeIconVisible)
                .anchor(0.5f, 0.5f)
                .icon(walkStationDescriptor));
        distance+=walkStep.getDistance();
        time+=walkStep.getDuration();
    }

    /**
     * 初始化线段属性
     */
    private void initPolylineOptions() {

        if(walkStationDescriptor == null) {
            walkStationDescriptor = getWalkBitmapDescriptor();
        }

        mPolylineOptions = null;

        mPolylineOptions = new PolylineOptions();
        mPolylineOptions.color(getWalkColor())
                        .width(getRouteWidth());
    }


    private void showPolyline() {
        addPolyLine(mPolylineOptions);
    }
}
