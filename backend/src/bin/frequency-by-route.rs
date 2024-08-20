use std::collections::HashMap;
use std::env;

use transit_model::{gtfs, Model};
use transit_model::objects::StopTime;

fn main() {
    let args: Vec<String> = env::args().collect();
    if args.len() <= 1 {
        eprintln!("Missing required argument for path");
        return;
    }

    for input in &args {
        if let Ok(model) = gtfs::read(input) {
            process_model(&model);
        }
    }
}

fn process_model(model: &Model) {
    let mut stop_times_by_route_and_stop: HashMap<String, HashMap<String, Vec<&StopTime>>> = HashMap::new();
    for (_, journey) in &model.vehicle_journeys {
        for stop_time in &journey.stop_times {
            let stop = &model.stop_points[stop_time.stop_point_idx];
            if let Some(stops) = stop_times_by_route_and_stop.get_mut(&journey.route_id) {
                if let Some(times) = stops.get_mut(&stop.id) {
                    times.push(&stop_time);
                } else {
                    stops.insert(stop.id.clone(), vec![&stop_time]);
                }
            } else {
                let mut stops = HashMap::new();
                stops.insert(stop.id.clone(), vec![stop_time]);
                stop_times_by_route_and_stop.insert(journey.route_id.clone(), stops);
            }
        }
    }



    /*for (_, route) in &model.routes {
        println!("{}: {}", route.id, route.name);
        for journey in model.vehicle_journeys.iter().filter(|(_, j)| j.route_id == route.id).map(|(_, j)| j) {
            if let Some(stop) = journey.stop_times.first() {
                println!("- {}: {} / {}", model.stop_points[stop.stop_point_idx].name, stop.departure_time, stop.arrival_time)
            }
        }
    }*/
}