<template>
  <div class="app-container">
    <el-row>
      <h4>线程池配置信息(设置)</h4>
      <el-table
          :data="[dataDetail.executorConfigSetting]"
          border
          style="width: 100%">
        <el-table-column align="center" property="corePoolSize" label="核心线程数"></el-table-column>
        <el-table-column align="center" property="maximumPoolSize" label="最大线程数"></el-table-column>
        <el-table-column align="center" property="keepAliveTime" label="最大存活时间(秒)"></el-table-column>
        <el-table-column align="center" property="rejectedStrategy" label="拒绝策略">
          <template slot-scope="scope">
            <span v-if="scope.row.rejectedStrategy === 'ABORT_POLICY'">抛出异常</span>
            <span v-if="scope.row.rejectedStrategy === 'CALLER_RUNS_POLICY'">由调用线程执行</span>
            <span v-if="scope.row.rejectedStrategy === 'DISCARD_POLICY'">丢弃当前任务</span>
            <span v-if="scope.row.rejectedStrategy === 'DISCARD_OLDEST_POLICY'">丢弃最旧任务</span>
          </template>
        </el-table-column>
        <el-table-column
            align="center"
            label="操作"
            fixed="right"
            width="120">
          <template slot-scope="scope">
            <el-button type="text" @click="showConfigure(scope.row)">
              配置
            </el-button>
          </template>
        </el-table-column>
      </el-table>
    </el-row>

    <el-row>
      <h4>线程池配置信息(实时)</h4>
      <el-table
          :data="[dataDetail.executorConfigMonitor]"
          border
          style="width: 100%">
        <el-table-column align="center" property="corePoolSize" label="核心线程数"></el-table-column>
        <el-table-column align="center" property="maximumPoolSize" label="最大线程数"></el-table-column>
        <el-table-column align="center" property="keepAliveTime" label="最大存活时间(秒)"></el-table-column>
        <el-table-column align="center" property="rejectedStrategy" label="拒绝策略">
          <template slot-scope="scope">
            <span v-if="scope.row.rejectedStrategy === 'ABORT_POLICY'">抛出异常</span>
            <span v-if="scope.row.rejectedStrategy === 'CALLER_RUNS_POLICY'">由调用线程执行</span>
            <span v-if="scope.row.rejectedStrategy === 'DISCARD_POLICY'">丢弃当前任务</span>
            <span v-if="scope.row.rejectedStrategy === 'DISCARD_OLDEST_POLICY'">丢弃最旧任务</span>
          </template>
        </el-table-column>
      </el-table>
    </el-row>

    <el-row>
      <h4>线程池统计信息(实时)</h4>
      <el-table
          :data="[dataDetail.executorStatisticsMonitor]"
          border
          style="width: 100%">
        <el-table-column prop="queueClass" label="队列类型"></el-table-column>
        <el-table-column prop="queueNodeCount" label="队列节点数"></el-table-column>
        <el-table-column prop="queueRemainingCapacity" label="队列剩余容量"></el-table-column>
        <el-table-column prop="poolSize" label="当前线程数"></el-table-column>
        <el-table-column prop="largestPoolSize" label="最大线程数（历史）"></el-table-column>
        <el-table-column prop="activeCount" label="执行中线程数"></el-table-column>
        <el-table-column prop="taskCount" label="已调度任务数"></el-table-column>
        <el-table-column prop="completedTaskCount" label="已完成任务数"></el-table-column>
      </el-table>
    </el-row>

    <el-row style="background:#fff;">
      <h4>线程池统计曲线</h4>
      <el-row>
        <el-button-group>
          <el-button autofocus size="small" type="primary" plain
                     @click="switchStatisticsDuration('IN_15_MINUTES')">
            最近15分钟
          </el-button>
          <el-button size="small" type="primary" plain
                     @click="switchStatisticsDuration('IN_1_HOURS')">
            最近1小时
          </el-button>
          <el-button size="small" type="primary" plain
                     @click="switchStatisticsDuration('IN_1_DAYS')">
            最近1天
          </el-button>
          <el-button size="small" type="primary" plain
                     @click="switchStatisticsDuration('IN_7_DAYS')">
            最近7天
          </el-button>
          <el-button size="small" type="primary" plain
                     @click="switchStatisticsDuration('IN_14_DAYS')">
            最近14天
          </el-button>
          <el-button size="small" type="primary" plain
                     @click="switchStatisticsDuration('IN_30_DAYS')">
            最近30天
          </el-button>
        </el-button-group>
      </el-row>
      <el-row>
        <div id="executorLineCharts" ref="executorLineCharts" style="width:100% ;height:480px;"></div>
      </el-row>
    </el-row>

    <el-dialog title="配置线程池" :visible.sync="configureVisible">
      <el-form :model="configureForm" label-position="right" label-width="120px">
        <el-form-item label="核心线程数">
          <el-input v-model="configureForm.executorConfig.corePoolSize" placeHolder="请输入" style="width: 300px"/>
        </el-form-item>
        <el-form-item label="最大线程数">
          <el-input v-model="configureForm.executorConfig.maximumPoolSize" placeHolder="请输入" style="width: 300px"/>
        </el-form-item>
        <el-form-item label="最大存活时间(秒)">
          <el-input v-model="configureForm.executorConfig.keepAliveTime" placeHolder="请输入" style="width: 300px"/>
        </el-form-item>
        <el-form-item label="拒绝策略">
          <el-select v-model="configureForm.executorConfig.rejectedStrategy" placeholder="请选择" style="width: 300px">
            <el-option :key="1" label="抛出异常" value="ABORT_POLICY"></el-option>
            <el-option :key="2" label="由调用线程执行" value="CALLER_RUNS_POLICY"></el-option>
            <el-option :key="3" label="丢弃当前任务" value="DISCARD_POLICY"></el-option>
            <el-option :key="4" label="丢弃最旧任务" value="DISCARD_OLDEST_POLICY"></el-option>
          </el-select>
        </el-form-item>
      </el-form>
      <div slot="footer" class="dialog-footer">
        <el-button @click="configureVisible = false">
          取 消
        </el-button>
        <el-button type="primary" @click="configure">
          确 定
        </el-button>
      </div>
    </el-dialog>
  </div>
</template>


<script>
import {CONSTANTS} from '@/api/common'
import {configureExecutor, getExecutorDetail, getExecutorStatisticsLineChart} from '@/api/executor'

export default {
  name: 'ExecutorDetail',
  data() {
    return {
      dataForm: {
        serverId: "",
        serverIp: "",
        executorId: "",
      },
      dataDetail: {
        executorConfigSetting: null,
        executorConfigMonitor: null,
        executorStatisticsMonitor: null
      },
      tableLoading: false,
      dataStatisticsForm: {
        serverId: "",
        serverIp: "",
        executorId: "",
        duration: "",
      },
      dataStatisticsChartPoints: [],
      executorStatisticsEChart: null,
      configureVisible: false,
      configureForm: {
        serverId: null,
        serverIp: null,
        executorId: null,
        executorConfig: {
          executorId: null,
          executorName: null,
          corePoolSize: null,
          maximumPoolSize: null,
          keepAliveTime: null,
          rejectedStrategy: null
        }
      }
    }
  },
  mounted: function () {
    this.dataForm.serverId = this.$route.query.serverId;
    this.dataForm.serverIp = this.$route.query.serverIp;
    this.dataForm.executorId = this.$route.query.executorId;

    this.configureForm.serverId = this.$route.query.serverId;
    this.configureForm.serverIp = this.$route.query.serverIp;
    this.configureForm.executorId = this.$route.query.executorId;

    this.dataStatisticsForm.serverId = this.$route.query.serverId;
    this.dataStatisticsForm.serverIp = this.$route.query.serverIp;
    this.dataStatisticsForm.executorId = this.$route.query.executorId;
    this.dataStatisticsForm.duration = 'IN_15_MINUTES';

    this.getDetail();
    this.initExecutorLineCharts();
    this.loadExecutorStatisticsPoints();
  },
  methods: {
    async getDetail() {
      this.tableLoading = true
      getExecutorDetail(this.dataForm)
          .then((response) => {
            if (response.code === CONSTANTS.SUCCESS_CODE) {
              this.dataDetail = response.data
            }
            this.tableLoading = false
          })
          .catch((err) => {
            this.tableLoading = false
          })
    },
    initExecutorLineCharts() {
      this.executorStatisticsEChart = require('echarts').init(document.getElementById("executorLineCharts"));
      this.executorStatisticsEChart.setOption({
        legend: {
          data: ['执行中线程数', '已完成任务数', '最大线程数(历史)', '当前线程数', '队列节点数', '队列剩余容量', '已调度任务数'],
          selected: {
            '执行中线程数': true,
            '已完成任务数': true,
            '最大线程数(历史)': true,
            '当前线程数': true,
            '队列节点数': true,
            '队列剩余容量': true,
            '已调度任务数': true,
          }
        },
        tooltip: {},
        xAxis: {
          data: this.dataStatisticsChartPoints.map(o => Object.keys(o)),
          name: '时间'
        },
        yAxis: {
          name: '数值'
        },
        series: [
          {name: '执行中线程数', smooth: true, type: 'line', data: []},
          {name: '已完成任务数', smooth: true, type: 'line', data: []},
          {name: '最大线程数(历史)', smooth: true, type: 'line', data: []},
          {name: '当前线程数', smooth: true, type: 'line', data: []},
          {name: '队列节点数', smooth: true, type: 'line', data: []},
          {name: '队列剩余容量', smooth: true, type: 'line', data: []},
          {name: '已调度任务数', smooth: true, type: 'line', data: []},
        ]
      })
    },
    async loadExecutorStatisticsPoints() {
      this.executorStatisticsEChart.showLoading();
      getExecutorStatisticsLineChart(this.dataStatisticsForm)
          .then((response) => {
            this.executorStatisticsEChart.hideLoading();
            if (response.code === CONSTANTS.SUCCESS_CODE) {
              this.dataStatisticsChartPoints = response.data.points
              const executorActiveCountPoints = this.dataStatisticsChartPoints.flatMap(o => Object.values(o).map(o => o.executorActiveCount))
              const executorCompletedTaskCountPoints = this.dataStatisticsChartPoints.flatMap(o => Object.values(o).map(o => o.executorCompletedTaskCount))
              const executorLargestPoolSizePoints = this.dataStatisticsChartPoints.flatMap(o => Object.values(o).map(o => o.executorLargestPoolSize))
              const executorPoolSizePoints = this.dataStatisticsChartPoints.flatMap(o => Object.values(o).map(o => o.executorPoolSize))
              const executorQueueNodeCountPoints = this.dataStatisticsChartPoints.flatMap(o => Object.values(o).map(o => o.executorQueueNodeCount))
              const executorQueueRemainCapacityPoints = this.dataStatisticsChartPoints.flatMap(o => Object.values(o).map(o => o.executorQueueRemainCapacity))
              const executorTaskCountPoints = this.dataStatisticsChartPoints.flatMap(o => Object.values(o).map(o => o.executorTaskCount))
              this.executorStatisticsEChart.setOption({
                xAxis: {
                  data: this.dataStatisticsChartPoints.map(o => Object.keys(o)),
                  name: '时间'
                },
                yAxis: {
                  name: '数值'
                },
                series: [
                  {name: '执行中线程数', data: executorActiveCountPoints},
                  {name: '已完成任务数', data: executorCompletedTaskCountPoints},
                  {name: '最大线程数(历史)', data: executorLargestPoolSizePoints},
                  {name: '当前线程数', data: executorPoolSizePoints},
                  {name: '队列节点数', data: executorQueueNodeCountPoints},
                  {name: '队列剩余容量', data: executorQueueRemainCapacityPoints},
                  {name: '已调度任务数', data: executorTaskCountPoints}
                ]
              })
            }
          })
          .catch((err) => {
          })
    },
    switchStatisticsDuration(duration) {
      this.dataStatisticsForm.duration = duration
      this.loadExecutorStatisticsPoints()
    },
    showConfigure(row) {
      this.configureVisible = true
      this.configureForm.executorConfig = {
        executorId: row.executorId,
        executorName: row.executorName,
        corePoolSize: row.corePoolSize,
        maximumPoolSize: row.maximumPoolSize,
        keepAliveTime: row.keepAliveTime,
        rejectedStrategy: row.rejectedStrategy,
      }
    },
    configure(row) {
      configureExecutor(this.configureForm)
          .then((response) => {
            if (response.code === CONSTANTS.SUCCESS_CODE) {
              this.configureVisible = false
              this.getDetail()
            }
          })
          .catch((err) => {
          })
    },
  }
}
</script>