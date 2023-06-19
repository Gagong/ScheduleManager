<script>
import getRowColor, {FRIDAY, MONDAY, SATURDAY, THURSDAY, TIME, TIMES, TUESDAY, WEDNESDAY} from "@/constants/constants";
import {SCHEDULE_API} from "@/axios/axios";

export default {
	name: 'GeneralSchedule',
	methods: {getRowColor},
	data: () => ({
		monday: MONDAY,
		tuesday: TUESDAY,
		wednesday: WEDNESDAY,
		thursday: THURSDAY,
		friday: FRIDAY,
		saturday: SATURDAY,
		items: [],
		time: TIME,
		times: TIMES,
	}),
	mounted() {
		SCHEDULE_API.get('getSchedule').then(resp => {
			console.log(resp)
			this.items = resp.data
		}).catch(e => {
			console.log(e)
			alert(e.response.data.message)
		});
	}
}
</script>

<template>
	<v-card>
		<v-card-title>Расписание</v-card-title>
		<v-card-text>
			<br/>
			<h3 style="text-align: center">Первая неделя</h3>
			<br/>
			<v-row>
				<v-col cols="1">
					<v-row>
						<v-col cols="12">
							<v-data-table
									:headers="time"
									:items="times"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="time"
									no-data-text="Данные отсутствуют"
							/>
						</v-col>
					</v-row>
				</v-col>
				<v-col cols="11">
					<v-row>
						<v-col cols="2">
							<v-data-table
									:headers="monday"
									:item-class="getRowColor"
									:items="items[0].cols[0].items"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="tuesday"
									:item-class="getRowColor"
									:items="items[0].cols[1].items"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="wednesday"
									:item-class="getRowColor"
									:items="items[0].cols[2].items"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="thursday"
									:item-class="getRowColor"
									:items="items[0].cols[3].items"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="friday"
									:item-class="getRowColor"
									:items="items[0].cols[4].items"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="saturday"
									:item-class="getRowColor"
									:items="items[0].cols[5].items"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
							/>
						</v-col>
					</v-row>
				</v-col>
			</v-row>
			<br/>
			<h3 style="text-align: center">Вторая неделя</h3>
			<br/>
			<v-row>
				<v-col cols="1">
					<v-row>
						<v-col cols="12">
							<v-data-table
									:headers="time"
									:items="times"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="time"
									no-data-text="Данные отсутствуют"
							/>
						</v-col>
					</v-row>
				</v-col>
				<v-col cols="11">
					<v-row>
						<v-col cols="2">
							<v-data-table
									:headers="monday"
									:item-class="getRowColor"
									:items="items[1].cols[0].items"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="tuesday"
									:item-class="getRowColor"
									:items="items[1].cols[1].items"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="wednesday"
									:item-class="getRowColor"
									:items="items[1].cols[2].items"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="thursday"
									:item-class="getRowColor"
									:items="items[1].cols[3].items"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="friday"
									:item-class="getRowColor"
									:items="items[1].cols[4].items"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
							/>
						</v-col>
						<v-col cols="2">
							<v-data-table
									:headers="saturday"
									:item-class="getRowColor"
									:items="items[1].cols[5].items"
									class="elevation-1 custom-table"
									dense
									hide-default-footer
									item-key="id"
									no-data-text="Данные отсутствуют"
							/>
						</v-col>
					</v-row>
				</v-col>
			</v-row>
		</v-card-text>
	</v-card>
</template>

<style lang="css" scoped>

</style>