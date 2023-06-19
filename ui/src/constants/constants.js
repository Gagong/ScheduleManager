export const MONDAY = [
	{
		text: 'Понедельник',
		align: 'center',
		sortable: false,
		value: 'value'
	},
]

export const TUESDAY = [
	{
		text: 'Вторник',
		align: 'center',
		sortable: false,
		value: 'value'
	},
]

export const WEDNESDAY = [
	{
		text: 'Среда',
		align: 'center',
		sortable: false,
		value: 'value'
	},
]

export const THURSDAY = [
	{
		text: 'Четверг',
		align: 'center',
		sortable: false,
		value: 'value'
	},
]

export const FRIDAY = [
	{
		text: 'Пятница',
		align: 'center',
		sortable: false,
		value: 'value'
	},
]

export const SATURDAY = [
	{
		text: 'Суббота',
		align: 'center',
		sortable: false,
		value: 'value'
	},
]

export const TIME = [
	{
		text: 'Время',
		align: 'center',
		sortable: false,
		value: 'time'
	},
]

export const TIMES = [
	{
		time: "8:00 - 9:35"
	},
	{
		time: "9:50 - 11:25"
	},
	{
		time: "11:40 - 13:15"
	},
	{
		time: "14:00 - 15:35"
	},
	{
		time: "15:50 - 17:25"
	},
	{
		time: "17:40 - 19:15"
	},
	{
		time: "19:25 - 21:00"
	}
]

export default function getRowColor(item) {
	if (item.disciplineType === null) {
		return;
	}
	switch (item.disciplineType.key) {
		case 'Лекция':
			return 'discipline_type_715'
		case 'Лабораторная работа 1':
		case 'Лабораторная работа 2':
			return 'discipline_type_716_717';
		case 'Семинар':
			return 'discipline_type_718';
		case 'Экзамен':
			return 'discipline_type_719';
		case 'Зачет':
			return 'discipline_type_720';
		case 'Курсовая работа':
			return 'discipline_type_721';
		case 'Курсовой проект':
			return 'discipline_type_722';
		default:
			return;
	}
}